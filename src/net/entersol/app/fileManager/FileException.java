/*
 * Copyright © 2013, Entersol Company. All rights reserved.
 * Copy righted material do not look at(!)
 */
package net.entersol.app.fileManager;
/**
 * This class extends Exception with out any change this is for future change possibility.
 */
public class FileException extends Exception{


	private static final long serialVersionUID = 1L;
	/**
	 * wraps parameterized Constructor of Exception class
	 */
	public FileException(String message){
		super(message);
	}
	/**
	 * wraps parameterized Constructor of Exception class
	 */
	public FileException(){
	}
	
}
