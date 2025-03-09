package net.entersol.tests;

import org.itemscript.core.values.JsonArray;

import net.entersol.app.fileManager.FileException;
import net.entersol.app.fileManager.FileIO;
import net.entersol.app.fileManager.FileRequestResult;
import net.entersol.iogate.IOGate;

public class TestFileIO {
	private IOGate iog;
	private FileIO fileIO;
	/**
	 * Conditions for this test to work:
	 * 1- Database 'Future' should be exist on the server
	 * 2- A user tano and pass tano  should be exist in General_Users table
	 * 3- the user tano should be in administrator group
	 * 4- An application with name 'File Manager' should be exist
	 * 
	 */
	public TestFileIO(){
		
		//iog = new IOGate("http://localhost/Future/php/index.php","polla", "sanar");
		iog = new IOGate("http://localhost/Future/php/index.php","tano", "tano");
		fileIO = new FileIO(iog, "File Manager", "administrator");
	}
	public void test_ls(){
		fileIO.ls("0", new FileRequestResult() {
			
			@Override
			public void onRequestSuccess(JsonArray result) {
				System.out.println(result);
				
			}
			
			@Override
			public void onRequestFailed(FileException fileException) {
				System.err.println(fileException);
				
			}
		});
	}
}
