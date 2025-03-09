package net.entersol.app.fileManager;

import java.util.LinkedList;

import net.entersol.iogate.IOGate;
import net.entersol.ui.dialog.ClosableDialog;

public class UploadManagerWinow extends ClosableDialog{
	
	public UploadManagerWinow(IOGate gate, String app, String group){
		this.add(new UploadManager(gate, app, group){
			@Override
			public void onFilesUploadSuccessed(LinkedList <String> files){
				onFilesUploadSuccessed(files);
			}
			@Override
			public void onFilesUploadFaild(FileException fe){
				onFilesUploadFaild(fe);
			} 
		});
	}
	public void onFilesUploadSuccessed(LinkedList <String> files){}
	public void onFilesUploadFaild(FileException fe){}
}
