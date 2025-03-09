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

import org.itemscript.core.ItemscriptSystem;
import org.itemscript.core.JsonSystem;
import org.itemscript.core.gwt.GwtConfig;
import org.itemscript.core.values.JsonObject;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.http.client.RequestBuilder.Method;

/**
 * <p>
 * This class gives a standard gate to the GWT to communicate with the server it uses built in classes to communicate 
 * over HTTP(S) and wraps them with nicer interface.</p>
 * <p>This class supports communication between client and server using JSON to communicate  data, status, and control messages  
 * With this class get and post requests can be made.</p>
 * <p>Also its possible to have public or private messages which means by public is anonymous persons and with private
 * Authorized user by name and password.</p>
 * <p>It is also possible to encrypt data that should be sent across the communication but it is recommended to use 
 * HTTPS if it is possible because it is faster, cleaner and more standardized </p>
 * @author Polla A. Fattah <br/><a href="mailto:polla@enterhosts.com">polla@enterhosts.com</a>
 */
public class IOGate{
	private String server;		//Path to the server
	//the user name and password are used for authorized access to the server or with private privacyLevel only
	private static String userName;	//if there is user name
	private static String password;	//if there is password
	
	private String privacyLevel;//private or public 
	private JsonObject header;	//The general standard header for this connection
	
	public static final JsonSystem SYSTEM = new ItemscriptSystem(new GwtConfig()); //This is the standard declared object by itemscript to handle JSON parsing 	
	//default server if nothing is provided
	private final static String DEFAULT_SERVER = "http://localhost/php/index.php";
	//the following 2 variables are used by default with the constructor and it depends on value of user name
	private final static String PUBLIC = "public";
	private final static String PRIVATE = "private";
	
// wrapping POST and GET variables from RequsetBuilder
	/**
	 * This static variable wraps RequesBilder.POST for sending requests with POST method
	 * @see IOGate#post(JsonObject, IOResponce)
	 * @see IOGate#securePost(JsonObject, IOResponce)
	 * @see IOGate#sendRequest(Method, String, String, IOResponce)
	 */
	public static final Method POST = RequestBuilder.POST;
	/**
	 * This static variable wraps RequesBilder.GET for sending requests with GET method
	 * @see IOGate#get(JsonObject, IOResponce)
	 * @see IOGate#secureGet(JsonObject, IOResponce)
	 * @see IOGate#sendRequest(Method, String, String, IOResponce)
	 */
	public static final Method GET  = RequestBuilder.GET;
//setting privacyLevels with these two final class variables 
	/**
	 * Used to indicate the connection is scrambled
	 * @see IOGate#securePost(JsonObject, IOResponce)
	 * @see IOGate#secureGet(JsonObject, IOResponce)
	 * @see IOGate#sendRequest(Method, String, String, IOResponce) 
	 */
	public static final String SECURE = "secure";
	/**
	 * Used to indicate the plain connection has been established 
	 * @see IOGate#get(JsonObject, IOResponce)
	 * @see IOGate#post(JsonObject, IOResponce)
	 * @see IOGate#sendRequest(Method, String, String, IOResponce)
	 */
	public static final String INSECURE = "insecure";
	
	/**
	 * This is default constructor that uses default server "localhost" to connect with
	 * and sets privacyLevel as public
	 */
	public IOGate(){
		this.server = DEFAULT_SERVER;
		createHeader();
	}
	/**
	 * This constructor uses the given server to connect with 
	 * @param server The server that should connect to
	 */
	public IOGate(String server){
		this.server = server;
		createHeader();
	}
	/**   
	 * This constructor uses default server "localhost" and privacyLevel is private 
	 * @param userName The user name
	 * @param password password for that user
	 */
	public IOGate(String userName, String password){
		this.server = DEFAULT_SERVER;
		createHeader(userName, password);
	}
	/**
	 * This constructor uses private privacyLevel and sets the server that should connect to
	 * @param server	//server URL
	 * @param userName The user name
	 * @param password password for that user
	 */
	public IOGate(String server, String userName, String password){
		this.server = server;
		createHeader(userName, password);
	}
	/**
	 * Sets the server that should be connected to
	 * @param server String server for this client
	 */
	public void setServer(String server){
		this.server = server;
	}
	/**
	 * returns server that connect to
	 * @return String that contain server URL
	 */
	public String getServer(){
		return this.server;
	}
	/**
	 * This method is general for sending all types of requests
	 * @param method POST or GET
	 * @param message any customized message it is not required to be in JSON format
	 * @param securityLevel public or private according to the existence of user name and password
	 * @param iores the answer will goes to this interfaces implementation
	 * @see IOGate#get(JsonObject, IOResponce)
	 * @see IOGate#post(JsonObject, IOResponce)
	 * @see IOGate#secureGet(JsonObject, IOResponce)
	 * @see IOGate#securePost(JsonObject, IOResponce)
	 */
	public void sendRequest(Method method, String message, final String securityLevel, final IOResponce iores){
		RequestBuilder builder = null;
		
		if(securityLevel == SECURE)
			message = Security.encrypt(message);

		message = URL.encode(message);
		if (method == IOGate.POST){
			builder = new RequestBuilder(RequestBuilder.POST, this.server);
			builder.setHeader("Content-type", "application/x-www-form-urlencoded");
		}
		else{
			builder = new RequestBuilder(RequestBuilder.GET, this.server + "?" + securityLevel + "=" + message);
		}
		try{
			builder.sendRequest(securityLevel + "=" + message, new RequestCallback(){
		        public void onError(Request request, Throwable exception){
		        	iores.onCallBackFailure(new IOGateException(exception.getMessage()));
		        	
		        }

		        public void onResponseReceived(Request request, Response response) {
		          if (200 == response.getStatusCode()){
		        	JsonObject jsonMessage = null;
		        	String message = response.getText();
		      		if(securityLevel == SECURE)
		    			message = Security.decrypt(message);
		      		try{
		      			jsonMessage = SYSTEM.parse(message).asObject();
		      			if(jsonMessage.getString("type").toString().equals("data")){
		      				iores.onCallBackSuccess(jsonMessage.getValue("context"));
		      			}
		      			else{
		      				iores.onCallBackFailure(new IOGateException(jsonMessage.getValue("context").toString(),1,"Server Script Error"));
		      			}
		      		}
		      		catch(Exception ex){
		      			ex.printStackTrace();
	      				iores.onCallBackFailure(new IOGateException(message, 2, "Server Script Error"));
		      		}
		          }
		          else {
		        	  iores.onCallBackFailure(new IOGateException("Server Responeded but with failur messsage", 
		        			  response.getStatusCode(), response.getStatusText()));
			        }
		        }
	      });
		}
		catch(RequestException ex){
			ex.printStackTrace();
			iores.onCallBackFailure(new IOGateException(ex.getMessage()));
		}
	}

	/**
	 * Uses POST method to send data it uses sendRequest method
	 * @param message The message that to be sent to the server it should be in JsonObject 
	 * @param iores the answer will goes to this interfaces implementation
	 * @see sendRequest 
	 */
	public void post(JsonObject message, IOResponce iores){
		sendRequest(IOGate.POST, createMessage(message), INSECURE, iores);
	}
	/**
	 * Uses GET method to send data  it uses sendRequest method
	 * @param message The message that to be sent to the server it should be in JsonObject 
	 * @param iores the answer will goes to this interfaces implementation
	 * @see sendRequest
	 */
	public void get(JsonObject message, IOResponce iores){
		sendRequest(IOGate.GET, createMessage(message), INSECURE, iores);
	}
	/**
	 * Uses POST method to send data and scramble the message.  it uses sendRequest method
	 * @param message The message that to be sent to the server it should be in JsonObject 
	 * @param iores the answer will goes to this interfaces implementation
	 * @see sendRequest 
	 */
	public void securePost(JsonObject message, IOResponce iores){
		sendRequest(IOGate.POST, createMessage(message), SECURE, iores);
	}
	/**
	 * Uses GET method to send data and scramble the message. it uses sendRequest method
	 * @param message The message that to be sent to the server it should be in JsonObject 
	 * @param iores the answer will goes to this interfaces implementation
	 * @see sendRequest 
	 */
	public void secureGet(JsonObject message, IOResponce iores){
		sendRequest(IOGate.GET, createMessage(message), SECURE, iores);
	}
// This method creates a copy of the header and appends real message to it then converts it to String
//this uses itemscript JsonObject to handle JSON easily 
	public String createMessage(JsonObject message){
	    JsonObject object = (JsonObject) header.copy();
	    object.put("messageBody", message);
		return object.toString();
	}
//creates header for this communication session and sets privacyLevel as public or private according to the 
//Existence of user name and password
	private void createHeader(){

			header =  SYSTEM.createObject();
			this.privacyLevel = PUBLIC;
			header.put("privacyLevel", privacyLevel);

	}
	private void createHeader(String userName, String password){
			header =  SYSTEM.createObject();
			this.privacyLevel = PRIVATE;
			header.put("privacyLevel", privacyLevel);
	    
			IOGate.userName = userName;
			IOGate.password = Security.md5(password);
	    	
	    	header.put("userName", IOGate.userName);
	    	header.put("password", IOGate.password);
	}

	/**
	 * returns status of privacyLevel (public or private)
	 * @return String of privacy level
	 */
	public String getPrivacyLevel(){
		return privacyLevel;
	}
	/**
	 * This method is used to change user name and password 
	 * @param userName String
	 * @param password  String
	 */
	public void setAuthorization(String userName, String password){
		createHeader(userName, password);
	}
}
