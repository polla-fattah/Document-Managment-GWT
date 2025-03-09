/*
 * Copyright © 2013, Entersol Company. All rights reserved.
 * Copy righted material do not look at(!)
 */

package net.entersol.app.fileManager;



import org.itemscript.core.values.JsonArray;
import org.itemscript.core.values.JsonObject;
import org.itemscript.core.values.JsonValue;
import net.entersol.indexeddb.IdbException;
import net.entersol.indexeddb.IdbTable;
import net.entersol.iogate.IOGate;


public class FileManager extends FileIO{
	private IdbTable filesTbl, sharedFilesTbl;
	private JsonArray lablesList;
	private JsonArray groupMembersList;

	public FileManager(IOGate gate, String app, String group){
		super(gate, app, group);
		filesTbl = new IdbTable("file", "idFile");
		filesTbl.addIndex("name");
		filesTbl.addIndex("parentDir");
		filesTbl.addIndex("starred");
		filesTbl.addIndex("lables");
		
		sharedFilesTbl = new IdbTable("sharedFile", "idFile");
		sharedFilesTbl.addIndex("name");
		sharedFilesTbl.addIndex("ownerName");
		sharedFilesTbl.addIndex("ownerId");
	}
	/**
	 * Refresh method works like ls method but it erases all previous stored data then calls ls this means it 
	 * forces ls to bring new data even if the data exist
	 */
	public void refresh(final String parentDir, final FileRequestResult fileRequestResult){
		try { 
			JsonArray childern = filesTbl.get("parentDir", parentDir);
			String keyValues[] = new String[childern.size()];
			for(int i = 0; i <  childern.size(); i++)
				keyValues[i] = childern.getString(i);
			filesTbl.delete(keyValues);
			
		}
		catch (IdbException e) { e.printStackTrace(); }
		ls(parentDir, fileRequestResult);
	}
	@Override
	public void ls(final String parentDir, final FileRequestResult fileRequestResult){
		try{

			if(filesTbl.get("parentDir", parentDir).isEmpty()){
				super.ls(parentDir, new FileRequestResultManager(fileRequestResult){
					@Override
					public void onRequestSuccess(JsonArray files) {
						try {
							filesTbl.insert(files);
						}
						catch (IdbException e) { e.printStackTrace(); }
						fileRequestResult.onRequestSuccess(files);		
				}});
			}
			else{
				fileRequestResult.onRequestSuccess(filesTbl.get("parentDir", parentDir));
			}
		}
		catch (IdbException e) { e.printStackTrace(); }
	}
	//Works fine but it needs to be explained that the Jsonarray files parameter is just for 
	//fileIds but other data is acceptable as long as the object contains fileId
	@Override
	public void rm(final JsonArray files, final FileRequestResult fileRequestResult){
		super.rm(files, new FileRequestResultManager(fileRequestResult){
			@Override
			public void onRequestSuccess(JsonArray filesResult) {
				String keys[] = new String[files.size()];
				for(int i = 0; i < files.size(); i++)
					keys[i] = files.getObject(i).getString("idFile");
				try {
					filesTbl.delete(keys);
				} 
				catch (IdbException e) {e.printStackTrace();}
				fileRequestResult.onRequestSuccess(filesResult);		
			}});
	}
	@Override
	public void rms(final JsonArray files, final FileRequestResult fileRequestResult){
		super.rms(files, new FileRequestResultManager(fileRequestResult){
			@Override
			public void onRequestSuccess(JsonArray filesResult) {
				String keys[] = new String[files.size()];
				for(int i = 0; i < files.size(); i++)
					keys[i] = files.getObject(i).getString("idFile");
				try {
					sharedFilesTbl.delete(keys);
				} 
				catch (IdbException e) {e.printStackTrace();}
				fileRequestResult.onRequestSuccess(filesResult);		
			}});
	}
	//Checked its working fine but there is need for documentation
	@Override
	public void rename(JsonArray files, String newName, final FileRequestResult fileRequestResult){
		super.rename(files, newName, new FileRequestResultManager(fileRequestResult){
			@Override
			public void onRequestSuccess(JsonArray filesResult) {
				fileTblUpdates(filesResult, "name");
				fileRequestResult.onRequestSuccess(filesResult);		
			}});
	}
	
	@Override
	public void move(final JsonArray files, final String newParent, final FileRequestResult fileRequestResult){
		super.move(files, newParent,  new FileRequestResultManager(fileRequestResult){
			@Override
			public void onRequestSuccess(JsonArray movedFiles) {
				for(JsonValue file : files){
					try {
						filesTbl.update(file.asObject().getString(ID_FILE), PARENT_DIR, newParent);
					} 
					catch (IdbException e) {e.printStackTrace();}
				}
				fileRequestResult.onRequestSuccess(movedFiles);		
			}});
	}
	
	//Checked its fine there is need for documentation and better error code for unavailable parent, exceeding size limit 
	@Override
	public void mkdir(String parentDir, String dirName, final FileRequestResult fileRequestResult){
		super.mkdir(parentDir, dirName,  new FileRequestResultManager(fileRequestResult){
			@Override
			public void onRequestSuccess(JsonArray files) {
				try {
					filesTbl.insert(files.getObject(0));
				} 
				catch (IdbException e) {e.printStackTrace();}

				fileRequestResult.onRequestSuccess(files);		
			}});
	}

	@Override
	public void addLable(final JsonArray files, final JsonArray lables, final FileRequestResult fileRequestResult){
		super.addLable(files, lables,  new FileRequestResultManager(fileRequestResult){
			@Override
			public void onRequestSuccess(JsonArray result) {
				fileTblUpdates(result, "lables");
				lablesList = null;
				fileRequestResult.onRequestSuccess(result);		

			}});
	}
	
	@Override
	public void removeLable(final JsonArray files, final JsonArray lables, final FileRequestResult fileRequestResult){
		super.removeLable(files, lables,  new FileRequestResultManager(fileRequestResult){
			@Override
			public void onRequestSuccess(JsonArray result){
				fileTblUpdates(result, "lables");
				lablesList = null;
				fileRequestResult.onRequestSuccess(result);		
			}});
	}
	
	@Override
	public void renameLable(String lable, String newLable,  final FileRequestResult fileRequestResult){
		super.renameLable(lable, newLable,  new FileRequestResultManager(fileRequestResult){
			@Override
			public void onRequestSuccess(JsonArray result) {
				fileTblUpdates(result, "lables");
				lablesList = null;
				fileRequestResult.onRequestSuccess(result);		
			}});
	}
	//This function returns the existing set of labels then updates the existing one by sending request to the server 
	// except for first time its sends request then returns the result 
	@Override
	public void listLables(final FileRequestResult fileRequestResult){
		if(lablesList != null)
			fileRequestResult.onRequestSuccess(lablesList);		

		super.listLables(new FileRequestResultManager(fileRequestResult){
			@Override
			public void onRequestSuccess(JsonArray result) {
				if(lablesList == null)
					fileRequestResult.onRequestSuccess(result);
				lablesList = result;	
			}});
	}
	@Override
	public void listGroupMembers(final FileRequestResult fileRequestResult){
		if(groupMembersList != null)
			fileRequestResult.onRequestSuccess(groupMembersList);		
		super.listGroupMembers( new FileRequestResultManager(fileRequestResult){
			@Override
			public void onRequestSuccess(JsonArray result) {
				if(groupMembersList == null)
					fileRequestResult.onRequestSuccess(result);
				groupMembersList = result;	
			}});
	}

	//Sharing files 
	//error handling needs to be more specific about type error 
	//namely  already shared, file not exist, user not exist
	//if its not sharable storageMod in Applications table then it could present better status code error
	@Override
	public void share(final JsonArray files, final JsonArray users, boolean doShare, final FileRequestResult fileRequestResult){
		super.share(files, users, doShare, new FileRequestResultManager(fileRequestResult){
			@Override
			public void onRequestSuccess(JsonArray filesResult) {
				fileTblUpdates(filesResult, "sharedWith");
				fileRequestResult.onRequestSuccess(filesResult);		
			}});
	}
	@Override
	public void sls(final FileRequestResult fileRequestResult){
		try{
			if(sharedFilesTbl.isEmpty()){
				super.sls(new FileRequestResultManager(fileRequestResult){
					@Override
					public void onRequestSuccess(JsonArray files) {
						try {
							System.out.println(files);
							sharedFilesTbl.insert(files);
							fileRequestResult.onRequestSuccess(files);
						}
						catch (IdbException e) { e.printStackTrace(); }
				}});
			}
			else{
				fileRequestResult.onRequestSuccess(sharedFilesTbl.getAll());
			}
		}
		catch (IdbException e) { e.printStackTrace(); }
	}

	public void refreshSared(final FileRequestResult fileRequestResult){
		try {
			sharedFilesTbl.deleteAll();
		} catch (IdbException e1) {	e1.printStackTrace();}
		sls(fileRequestResult);
	}

	@Override
	public void star(JsonArray files, boolean star, final FileRequestResult fileRequestResult){
		super.star(files, star,  new FileRequestResultManager(fileRequestResult){
			@Override
			public void onRequestSuccess(JsonArray results) {
				fileRequestResult.onRequestSuccess(results);
				fileTblUpdates(results, "starred");

			}});
	}
	@Override
	public void editDescription(final JsonObject file, final String description,  final FileRequestResult fileRequestResult){
		super.editDescription(file, description,  new FileRequestResultManager(fileRequestResult){
			@Override
			public void onRequestSuccess(JsonArray files) {
				try{
					filesTbl.update(file.getString("idFile"), "description", description); 
				}
				catch (IdbException e) { e.printStackTrace(); }
				fileRequestResult.onRequestSuccess(files);
			}});
	}
	
	private abstract class FileRequestResultManager implements FileRequestResult{
		private FileRequestResult fileRequestResult;
		public FileRequestResultManager(FileRequestResult fileRequestResult){
			this.fileRequestResult = fileRequestResult;
		}
		@Override
		public void onRequestFailed(FileException fileException) {
			fileRequestResult.onRequestFailed(fileException);
		}		
	}
	private void fileTblUpdates(JsonArray filesResult, String column){
		JsonObject fileObj;
		try {
			for(JsonValue nextFile : filesResult){
				fileObj = nextFile.asObject(); 
				filesTbl.update(fileObj.getString(ID_FILE), column, fileObj.getString("newValue"));
			}
		}
		catch (IdbException e) {e.printStackTrace();}
	}

	private void fileTblUpdates(JsonArray filesResult, String column, String value){
		JsonObject fileObj;
		try {
			for(JsonValue nextFile : filesResult){
				fileObj = nextFile.asObject(); 
				filesTbl.update(fileObj.getString(ID_FILE), column, value);
			}
		}
		catch (IdbException e) {e.printStackTrace();}
	}

}
