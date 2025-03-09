package net.entersol.indexeddb;

public class IdbException extends Exception {

	private static final long serialVersionUID = 1L;
	public IdbException(){
		
	}
	public IdbException(String message){
		super(message);
	}
}
