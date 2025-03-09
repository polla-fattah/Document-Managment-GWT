/*
 * Copyright © 2013, Entersol Company. All rights reserved.
 * Copy righted material do not look at(!)
 */

package net.entersol.app.fileManager;

import net.entersol.iogate.IOGate;
import net.entersol.iogate.IOGateException;
import net.entersol.iogate.IOResponce;
import net.entersol.iogate.IOService;
import org.itemscript.core.values.JsonArray;
import org.itemscript.core.values.JsonObject;
import org.itemscript.core.values.JsonValue;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;

/**
 * This class communicates with the server by sending commands 
 * in the form of Json string that contains commands and arguments for that command
 * The JSON string that is wrapped in the sending request will have this form
 * 
 * {
 * 	command: selected_command,
 * 	argument:[argument(s)]
 * }
 * @author Polla Fattah
 *
 */
public class FileIO {
	// all these strings are used as keys to send data to the server. their pare are exist on the server 
	protected static final String NEW_NAME = "newName";
	protected static final String NEW_PARENT = "newParent";
	protected static final String GROUP = "group";
	protected static final String APPLICATION = "app";
	protected static final String NEW_LABLE = "newLable";
	protected static final String LABLE = "lable";
	protected static final String ID_FILE = "idFile";
	protected static final String PARENT_DIR = "parentDir";
	protected static final String STAR = "star";
	protected static final String DESCRIPTION = "desc";
	protected static final String NAME = "name";
	protected static final String SHARING_WITH = "sharingWith";
	protected static final String PHRASE = "phrase";
	protected static final String LOCK = "lock";
	protected static final String IMAGE = "image";
	
	private IOService service;
	private String app;
	private String group;
	/**
	 * The only constructor for FileIO which creates a {@link IOService} object under the skin to communicant with the provided server
	 * @param gate :server to connect with 
	 * @param app : which application on that server 
	 * @param group : The group that user belongs to.
	 */
	public FileIO(IOGate gate, String app, String group){
		service = new IOService(gate, "", "FileIO", "");
		this.app = app;
		this.group = group;
	}
	
	//Basic file operations 
	public void ls(String parentDir, FileRequestResult fileRequestResult){
		JsonObject argument = createArgument();
		argument.put(PARENT_DIR, parentDir);
		send("ls", argument, fileRequestResult, true);
	}
	public void rm(JsonArray files, FileRequestResult fileRequestResult){
		removeFile(files, "rm",fileRequestResult);
	}
	public void rms(JsonArray files, FileRequestResult fileRequestResult){
		removeFile(files, "rms",fileRequestResult);
	}
	private void removeFile(JsonArray files, String method, FileRequestResult fileRequestResult){
		if(files.isEmpty())
			return;
		JsonObject argument = createArgument();
		argument.put(ID_FILE, getColumn(files, ID_FILE));
		send(method, argument, fileRequestResult, false);
	}
	public void rename(JsonArray files, String newName, FileRequestResult fileRequestResult){
		if(files.isEmpty())
			return;
		JsonObject argument = createArgument();
		argument.put(ID_FILE, getColumn(files, ID_FILE));
		argument.p(NEW_NAME, newName);
		send("rename", argument, fileRequestResult, true);
	}
	public void move(JsonArray files, String newParent, FileRequestResult fileRequestResult){
		if(files.isEmpty())
			return;
		JsonObject argument = createArgument();
		argument.put(ID_FILE, getColumn(files, ID_FILE));
		argument.p(NEW_PARENT, newParent);
		send("move", argument, fileRequestResult, false);
	}
	
	// Directory Methods
	public void mkdir(String parentDir, String dirName, FileRequestResult fileRequestResult){
		if(parentDir.trim().equals("") || dirName.trim().equals("") )
			return;
		JsonObject argument = createArgument();
		argument.put(PARENT_DIR, parentDir);
		argument.p(NAME, dirName);
		send("mkdir", argument, fileRequestResult, true);
	}
	
	// Lable related methods
	public void addLable(JsonArray files, JsonArray lables, FileRequestResult fileRequestResult){
		changeLable(files,lables,fileRequestResult, "addLable");
	}
	public void removeLable(JsonArray files, JsonArray lables, FileRequestResult fileRequestResult){
		changeLable(files,lables,fileRequestResult, "removeLable");
	}
	public void renameLable(String lable, String newLable,  FileRequestResult fileRequestResult){
		if(lable == null || lable.isEmpty()|| newLable == null || newLable.isEmpty())
			return;
		JsonObject argument = createArgument();
		argument.put(NEW_LABLE, newLable);
		argument.put(LABLE, lable);
		send("renameLable", argument, fileRequestResult, false);
	}
	public void listLables(FileRequestResult fileRequestResult){
		JsonObject argument = createArgument();
		send("listLables", argument, fileRequestResult, true);
	}
	public void lls(String lable, FileRequestResult fileRequestResult){
		if(lable == null || lable.isEmpty())
			return;
		JsonObject argument = createArgument();
		argument.put(LABLE, lable);
		send("lls", argument, fileRequestResult, true);
	}
	
   	//Sharing methods
	public void share(JsonArray files, JsonArray users, boolean doShare, FileRequestResult fileRequestResult){
		if(files == null || users == null)
			fileRequestResult.onRequestFailed(new FileException("Useer and files argument can not be null"));
		if(files.isEmpty()||  users.isEmpty())
			fileRequestResult.onRequestFailed(new FileException("Useer and files argument can not be empty"));
		String sharing = doShare? "share" : "unShare";
		JsonObject argument = createArgument();
		argument.put(ID_FILE, getColumn(files, ID_FILE));
		argument.put(SHARING_WITH, users);
		send(sharing, argument, fileRequestResult, true);
	}

	public void sls(FileRequestResult fileRequestResult){
		JsonObject argument = createArgument();
		send("sls", argument, fileRequestResult, true);
	}
	public void listGroupMembers(FileRequestResult fileRequestResult){
		JsonObject argument = createArgument();
		send("listGroupMembers", argument, fileRequestResult, true);
	}

	// Searching for files
	public void find (String phrase, FileRequestResult fileRequestResult){
		if(phrase == null || phrase.isEmpty())
			return;
		JsonObject argument = createArgument();
		argument.put(PHRASE, phrase);
		send("find", argument, fileRequestResult, true);
	}
	public void findShared(String phrase, FileRequestResult fileRequestResult){
		if(phrase == null || phrase.isEmpty())
			return;
		JsonObject argument = createArgument();
		argument.put(PHRASE, phrase);
		send("findShared", argument, fileRequestResult, true);
	}

	public void star(JsonArray files, boolean star, FileRequestResult fileRequestResult){
		if(files.isEmpty())
			return;
		JsonObject argument = createArgument();
		argument.put(ID_FILE, getColumn(files, ID_FILE));
		String strStar = star ? "1": "0";
		argument.p(STAR, strStar);
		send("star", argument, fileRequestResult, true);
	}
	public void editDescription(JsonObject file, String description,  FileRequestResult fileRequestResult){
		if(description.trim().equals(""))
			return;
		JsonObject argument = createArgument();
		argument.put(ID_FILE, file.getString(ID_FILE));
		argument.p(DESCRIPTION, description);
		send("editDescription", argument, fileRequestResult, false);
		
	}


	// Downloading and loading methods
	public void downloadFile(JsonObject file){
		if(file == null || file.isEmpty())
			return;
		JsonObject argument = createArgument();
		argument.put(ID_FILE, file.getString(ID_FILE));
		service.setFunction("downloadFile");
		String s = 	service.getGate().createMessage(service.createMessage(argument)).toString();
		s = URL.encode(s);
		s = "?insecure=" + s;
		
		Window.open(service.getGate().getServer() + s, "Downloads", "menubar=no,location=no,resizable=no,scrollbars=no,status=no,hight=100,width=130");
	}
	public void downloadImage(JsonObject file, boolean thumb, final ImageRequestResult imageRequestResult){
		if(file == null || file.isEmpty())
			return;
		JsonObject argument = createArgument();
		argument.put(ID_FILE, file.getString(ID_FILE));
		argument.put(IMAGE, (thumb?"thumb":"real"));
		service.setFunction("downloadImage");
		service.post(argument, new IOResponce(){
			@Override
			public void onCallBackSuccess(JsonValue message){
				byte []bytes = message.asObject().getBinary(IMAGE);
				String base64 = "data:image/jpg;base64," + Base64.encodeToString(bytes,false);
				Image image = new Image();
				image.setUrl(base64);
				imageRequestResult.onRequestSuccess(image);
			}
			@Override
			public void onCallBackFailure(IOGateException ioge) {
				imageRequestResult.onRequestFailed(new FileException(ioge.getMessage()));
			}
		});
	}
	
	//Private helper methods
	private void send(String function, JsonObject argument, final FileRequestResult fileRequestResult, final boolean getResult){
		service.setFunction(function);
		service.post(argument, new IOResponce(){
			@Override
			public void onCallBackSuccess(JsonValue message){
				if(getResult)
					fileRequestResult.onRequestSuccess(message.asArray());
				else
					fileRequestResult.onRequestSuccess(null);
			}
			@Override
			public void onCallBackFailure(IOGateException ioge) {
				fileRequestResult.onRequestFailed(new FileException(ioge.getMessage()));
			}
		});
	}
	private JsonArray getColumn(JsonArray table, String colName){
		JsonArray col = IOGate.SYSTEM.createArray();
		for (JsonValue file : table)
			col.add(file.asObject().getString(colName));
		return col;
	}
	private void changeLable(JsonArray files, JsonArray lables, FileRequestResult fileRequestResult, String method){
		if(files == null || files.isEmpty()|| lables == null || lables.isEmpty())
			return;
		JsonObject argument = createArgument();
		argument.put(ID_FILE, getColumn(files, ID_FILE));
		argument.put(LABLE, lables);
		send(method, argument, fileRequestResult, true);
	}
	private JsonObject createArgument(){
		JsonObject argument = IOGate.SYSTEM.createObject();
		argument.put(APPLICATION, app);
		argument.put(GROUP, group);
		return argument;
	}
}
