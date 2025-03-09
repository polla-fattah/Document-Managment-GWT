/*
 * Copyright © 2013, Entersol Company. All rights reserved.
 * Copy righted material do not look at(!)
 */

package net.entersol.app.fileManager;
/**
 * 
 		final FileUploader fup = new FileUploader(iog, "File Manager", "administrator"){
			@Override
			public void onFileUploadFaild(FileException fe) {
				//super.onFileUploadFaild(fe);
				l.setText(fe.getMessage());
				System.out.println(fe);
			}
		};
		Button b = new Button("upload");
		b.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				fup.submit();
			}
		});
 */
import net.entersol.iogate.IOGate;
import net.entersol.iogate.IOService;

import org.itemscript.core.values.JsonObject;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Image;

public class FileUploader extends FormPanel {
	private FileUpload file = new FileUpload();
	private Hidden hidden = new Hidden("insecure");
	private Image img = new Image();

	private IOService service;
	private IOGate gate;
	
	private String app;
	private String group;
	private String parentDir;
	private String thumbHight;
	private String thumbWidth;
	
	private static final String WAITING = "Theme/waiting.gif";
	private static final String SUCCESS = "Theme/success.png";
	private static final String FAIL = "Theme/error.png";
	private static final String BLANK = "Theme/spacer.gif";
	
	private static final String PARENT_DIR = "parent";
	private static final String GROUP = "group";
	private static final String APPLICATION = "app";
	private static final String THUMB_HIGHT = "th_h";
	private static final String THUMB_WIDTH = "th_w";

	public FileUploader(IOGate gate, String app, String group){

		this.gate = gate;
		this.app = app;
		this.group = group;
		
		service = new IOService(gate, "", "FileUpload", "uploadListner");
		creteForm();
	}
    private void creteForm(){
		setAction(gate.getServer());
		setEncoding(FormPanel.ENCODING_MULTIPART);
		setMethod(FormPanel.METHOD_POST);
		
		file.setName("file");
		file.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				img.setUrl(BLANK);
			}				
		});
		img.setSize("20PX", "20PX");
		img.setStyleName("img-file-loading");
		img.setUrl(BLANK);
		
		FlexTable table = new FlexTable();
		table.setWidget(1, 0, img);
		table.setWidget(1, 1, file);
		table.setWidget(1, 2, hidden);
		
		setWidget(table);
		setStyleName("FileUloader");
		addSubmitCompleteHandler(new SubmitCompleteHandlerImp());
		addSubmitHandler(new SubmitHandlerImp());

    }
	public String getFileName(){
		return file.getFilename();
	}
	public void onFileUploadSuccessed(String fileName){}
	public void onFileUploadFaild(FileException fe){}
	
	public String getParentDirId() {
		return parentDir;
	}

	public void setParentDirId(String parentDirId) {
		this.parentDir = parentDirId;
	}

	public String getThumbHight() {
		return thumbHight;
	}
	public void setThumbHight(String thumbHight) {
		this.thumbHight = thumbHight;
	}

	public String getThumbWidth() {
		return thumbWidth;
	}
	public void setThumbWidth(String thumbWidth) {
		this.thumbWidth = thumbWidth;
	}
	public void setThumbDimention(String h, String w){
		this.thumbHight = h;
		this.thumbWidth = w;
	}
	public boolean isEmpty() {
		return file.getFilename().equals("");
	}
	private class SubmitCompleteHandlerImp implements SubmitCompleteHandler{
		@Override
		public void onSubmitComplete(SubmitCompleteEvent event) {
			JsonObject jsonResult = null;
			String result = event.getResults();
			try{
				result = result.replaceAll("\\<.*?>","");
				result = result.substring(0, result.indexOf("}") + 1);
				jsonResult = IOGate.SYSTEM.parse(result).asObject();
			}
			catch(Exception e){
				System.out.println(event.getResults());
				onFileUploadFaild(new FileException(e.getMessage() + "\n" +  event.getResults() ));
				return;
			}

			if(jsonResult.getString("type").equals("error")){
				img.setUrl(FAIL);
				onFileUploadFaild(new FileException(jsonResult.getString("context")));
				return;
			}
			else{
				img.setUrl(SUCCESS);
				onFileUploadSuccessed(jsonResult.getString("context"));
			}
		}
    }
    private class SubmitHandlerImp implements SubmitHandler{
		@Override
		public void onSubmit(SubmitEvent event){
			if(isEmpty()){
				event.cancel();
				return;
			}
			img.setUrl(WAITING);
			JsonObject data = IOGate.SYSTEM.createObject();
			data.put(APPLICATION, app);
			data.put(GROUP, group);
			data.put(GROUP, group);
			data.put(PARENT_DIR, parentDir);
			data.put(THUMB_HIGHT, thumbHight);
			data.put(THUMB_WIDTH, thumbWidth);

			hidden.setValue(gate.createMessage(service.createMessage(data)));
		}
    }
}
