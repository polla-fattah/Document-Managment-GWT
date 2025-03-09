package net.entersol.app.fileManager;

import java.util.LinkedList;

import net.entersol.iogate.IOGate;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.HasDirection.Direction;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;


public class UploadManager extends Composite {
   private Button addUploader = new Button("+");
   private Button submit = new Button("Submit");
   private IOGate gate;
   private String app, group; 
   private VerticalPanel uploadsPanel;
   private Label label= new Label();
   private LinkedList<FileUploader> uploadersList = new LinkedList<FileUploader>();
   private int uploadCount = 0;
   private LinkedList<String> successFileNames = new LinkedList<String>();
   /**
   * Constructs an OptionalTextBox with the given caption 
   * on the check.
   * @param caption the caption to be displayed with the check box
   */
   public UploadManager(IOGate gate, String app, String group) {
	   this.gate = gate;
	   this.app = app;
	   this.group = group;

	   initWidget(getGeneralPanel());
   }
   private VerticalPanel getGeneralPanel(){
	      // Place the check above the text box using a vertical panel.
		  VerticalPanel generalPanel = new VerticalPanel();
		  uploadsPanel = new VerticalPanel();
		  generalPanel.setSpacing(5);
		  addUploader();
		  
		  generalPanel.add(label);
		  generalPanel.add(uploadsPanel);
		  generalPanel.add(getCommands());
		  
		  return generalPanel;
   }
   private HorizontalPanel getCommands(){
	   HorizontalPanel commandPanel = new HorizontalPanel();
	   commandPanel.setWidth("90%");
	   commandPanel.setSpacing(5);
	   
	   submit.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				for(FileUploader fileUp: uploadersList){
					if(!fileUp.isEmpty()){
						uploadCount++;
						fileUp.submit();
					}
				}
			}
		});
	   addUploader.addClickHandler(new ClickHandler() {
		@Override
		public void onClick(ClickEvent event){
			addUploader();
		}
	   });
	   commandPanel.add(addUploader);
	   commandPanel.add(submit);
	   
	   return commandPanel;
   }
   public void onFilesUploadSuccessed(LinkedList <String> files){}
   public void onFilesUploadFaild(FileException fe){}
   private void sendMessages(){
	   if(!successFileNames.isEmpty())
	   onFilesUploadSuccessed(successFileNames);
	   successFileNames.element();
   }
   private void addUploader(){
		FileUploader fileUp = new FileUploader(gate, app, group){
			@Override
			public void onFileUploadSuccessed(String fileName){
				successFileNames.add(fileName);
				uploadCount--;
				if(uploadCount == 0)
					sendMessages();
			}
			@Override
			public void onFileUploadFaild(FileException fe){
				uploadCount--;
				if(uploadCount == 0)
					sendMessages();
				onFilesUploadFaild(fe);
				// Can be enhanced by set and getStackTrace so that we can know all faults
			}
		};
		uploadersList.add(fileUp);
		uploadsPanel.add(fileUp);
   }
   public void setLabelTest(String text){
	   label.setText(text);
   }
   public void setLabelTest(String text, Direction dir){
	   label.setText(text, dir);
   }
   public void showAddUplaoder(boolean visible){
	   addUploader.setVisible(visible);
   }
}