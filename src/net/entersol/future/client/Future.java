package net.entersol.future.client;

import java.util.Date;

import org.itemscript.core.values.JsonArray;
import org.itemscript.core.values.JsonValue;

import net.entersol.app.fileManager.FileException;
import net.entersol.app.fileManager.FileIcon;
import net.entersol.app.fileManager.FileManager;
import net.entersol.app.fileManager.FileRequestResult;
import net.entersol.app.fileManager.FileUploader;
import net.entersol.app.fileManager.UploadManager;
import net.entersol.iogate.IOGate;
import net.entersol.ui.dialog.ClosableDialog;
import net.entersol.ui.dialog.Confirm;
import net.entersol.ui.dialog.Message;
import net.entersol.ui.dialog.Waiting;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dev.jjs.ast.js.JsonObject;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class Future implements EntryPoint {
	JsonArray myFiles;
	FileManager fmg;
	final int LOOP = 0;
	long time;
	protected Label l = new Label();
	String outPut;
	long i , oldt, newt;
	ScrollPanel panel = new ScrollPanel();
	VerticalPanel vpanel = new VerticalPanel();
	public void onModuleLoad() {

		//IOGate iog = new IOGate("http://localhost/Future/php/index.php","polla", "sanar");
		IOGate iog = new IOGate("http://localhost/Future/php/index.php","tano", "tano");
		fmg = new FileManager(iog, "File Manager", "administrator");
		panel.setSize("400px", "700px");
		panel.setAlwaysShowScrollBars(true);
		fmg.ls("", new FileRequestResult() {
			
			@Override
			public void onRequestSuccess(JsonArray result) {
				System.out.println(result);
				for(JsonValue file: result){
					vpanel.add(new FileIcon(file.asObject()));
				}
			}
			
			@Override
			public void onRequestFailed(FileException fileException) {
				System.out.println(fileException);
				
			}
		});
		panel.add(vpanel);
		RootPanel.get().add(panel);

		
		//UploadManager upmg = new UploadManager(iog, "File Manager", "administrator");
		//ClosableDialog dialog = new ClosableDialog();
		//dialog.setText("This is test");
		//dialog.add(upmg);
		//dialog.center();
		//Message m = new Message(Message.ERROR , "Hi there");
		//m.setTitle("Polla");
		//m.show();

		//RootPanel.get().add(fup);
		//RootPanel.get().add(b);


	}
}

/** 
 * 	private void test(final int i){
		if(i == 0)
			time = (new Date()).getTime();
		JsonArray fi = IOGate.SYSTEM.createArray();
		JsonArray la = IOGate.SYSTEM.createArray();
		la.a("tano");
		la.a("babo");
		fi.a(myFiles.getObject(0).copy());
//		fi.a(myFiles.getObject(1).copy());
		System.out.println("FIFIFIFIFIFIFIFIFIIFIFIFIFIFIFIFIFIFI");
		System.out.println(fi);
		System.out.println("FIFIFIFIFIFIFIFIFIIFIFIFIFIFIFIFIFIFI");
		
		fmg.rms(fi, new FileRequestResult(){
			@Override
			public void onRequestSuccess(JsonArray result){
				System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
				System.out.println(result);
				System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

				fmg.sls(new FileRequestResult(){
					@Override
					public void onRequestSuccess(JsonArray files) {						
						System.out.println("2222222222222222222222222222222222222222222");
						System.out.println(files);
						System.out.println("2222222222222222222222222222222222222222222");
					}
					
					@Override
					public void onRequestFailed(FileException fileException) {
						System.out.println(fileException.getMessage());
				}});
			}
			@Override
			public void onRequestFailed(FileException fileException) {
				System.out.println(fileException.getMessage());
			}});
	}


		final FileManager fm = new FileManager(iog);
		File f1 = new File();

		f1.setId("12");
		f1.setName("Name.txt");
		f1.setDescription("Description");
		f1.setPath("polla/sanar/");

		File f2 = new File();
		f2.setId("123");
		f2.setName("tano.txt");
		f2.setDescription("Defgalsdfkj asd;lk asdkfj ;llakfskasdjf asdifhu");
		f2.setPath("/sanar/");

		fio.rm(myFiles, new FileRequestResult(){

			@Override
			public void onRequestSuccess(JsonArray files) {
				System.out.println(files);
			}

			@Override
			public void onRequestFailed(FileException fileException) {
				System.out.println(fileException.getMessage());
				
			}});
*/
/*
 * 		 Timer timer = new Timer() {
		      public void run() {
				for(i = 0; i < 1; i++){
					start = new Date().getTime();
					fmg.ls("0", new FileRequestResult(){
						@Override
						public void onRequestSuccess(JsonArray files) {
						
							
							myFiles = files;
						//	outPut += files.toString();
						//	l.setText(outPut);
							System.out.println("------------------------------------------");
							System.out.println(files);
							System.out.println("++++++++++++++++++++++++++++++++++");
							test();

							System.out.println( i + " - " + ((new Date()).getTime() - start ));
							
						}
						
						@Override
						public void onRequestFailed(FileException fileException) {
							System.out.println(fileException.getMessage());
					}});
				}
		      }
		 };
		 timer.schedule(3000);		 Timer timer = new Timer() {
		      public void run() {
				for(i = 0; i < 1; i++){
					start = new Date().getTime();
					fmg.ls("0", new FileRequestResult(){
						@Override
						public void onRequestSuccess(JsonArray files) {
						
							
							myFiles = files;
						//	outPut += files.toString();
						//	l.setText(outPut);
							System.out.println("------------------------------------------");
							System.out.println(files);
							System.out.println("++++++++++++++++++++++++++++++++++");
							test();

							System.out.println( i + " - " + ((new Date()).getTime() - start ));
							
						}
						
						@Override
						public void onRequestFailed(FileException fileException) {
							System.out.println(fileException.getMessage());
					}});
				}
		      }
		 };
		 timer.schedule(3000);
 * 
 */
