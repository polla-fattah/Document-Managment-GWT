/*
 * Copyright © 2011, Entersol Company. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright notice,
 *       this list of conditions and the following disclaimer in the documentation
 *       and/or other materials provided with the distribution.
 *     * Neither the names of entersol Company, enterhosts, gdbc
 *       nor the names of its contributors may be used to endorse or promote products derived
 *       from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * 
 * Author: Polla A. Fattah
 */

package net.entersol.iogate;

/**
 * This exception class contains information about the failure of the connection that 
 * has been established between client and server 
 * @author Polla A. Fattah <br/><a href="mailto:polla@enterhosts.com">polla@enterhosts.com</a>
 */
public class IOGateException extends Exception {
	
	private static final long serialVersionUID = -1155090592313982524L;
	private int statusCode;
	private String statusText;
	/**
	 * Default constructor
	 */
	public IOGateException() {}
	
	/**
	 * This constructor sets the message for the exception
	 * @param message the message that should occur with this exception
	 */
	public IOGateException(String message) {
		super(message);
	}
	/**
	 * This constructor allows to specify message, status code of the server and status text
	 * @param message the message that should occur with this exception
	 * @param statusCode the code that is returned from server
	 * @param statusText the text corresponding to the status of the request
	 */
	public IOGateException(String message, int statusCode, String statusText) {
		super(message);
		this.statusCode = statusCode;
		this.statusText = statusText;
	}
	/**
	 * this method returns a string contain information about the exception type
	 * @return String message with status code + status text 
	 */
	@Override 
	public String getMessage(){
		return super.getMessage()+ "\nStatus Code = " + getStatusCode() 
							+ "\n Status Text = " + getStatusText(); 
	}
	/**
	 * set a value to the status code
	 * @param statusCode 
	 */
	public void setStatusCode(int statusCode){
		this.statusCode = statusCode;
	}
	/**
	 * sets a value for the text message
	 * @param statusText
	 */
	public void setStatusText(String statusText){
		this.statusText = statusText;
	}
	/**
	 * returns an integer number represents HTTP status code
	 * @return int code
	 */
	public int getStatusCode(){
		return this.statusCode;
	}
	/**
	 * returns String that contains status of the HTTP response 
	 * @return String status text
	 */
	public String getStatusText(){
		return this.statusText;
	}

}
