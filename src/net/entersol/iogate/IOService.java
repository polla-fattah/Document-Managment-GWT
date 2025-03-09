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

import org.itemscript.core.gwt.GwtSystem;
import org.itemscript.core.values.JsonObject;

/**
 * <p>This class creates an abstraction layer upon IOGate connection this class aims specific
 * to the services not to the server. the service can be in the form of:</P>
 * <ol>
 * <li> Class in a file
 * <li> Class in a file and needs include to other file service
 * <li> Function with a single JSON parameter; inside a file that its required for this function
 * <li> A file that just needs an include to work
 * </ol>
 * @see IOGate
 * @author Polla A. Fattah <br/><a href="mailto:polla@enterhosts.com">polla@enterhosts.com</a>
 */

public class IOService {
	private IOGate gate;
	private JsonObject service;

	public static final String INCLUDE = "include";
	public static final String CLASS = "class";
	public static final String FUNCTION = "function";
	public static final String DATA = "data";
	
	/**
	 * Default constructor; this constructor creates a connection
	 * to the default server (localhost) without any service selected
	 */
	public IOService(){
		service = GwtSystem.SYSTEM.createObject();
		this.gate = new IOGate();
	}
	/**
	 * This constructor connects to the gate that provided
	 * but without selecting any service.
	 * @param g the connection to the server
	 */
	public IOService(IOGate g){
		service = GwtSystem.SYSTEM.createObject();
		this.gate = g;
	}
	/**
	 * This constructor connects to the given gate and selects the service
	 * @param g the server gate 
	 * @param include the file service
	 * @param clas the class service
	 * @param function the function service 
	 */
	public IOService(IOGate g, String include, String clas, String function){
		this.gate = g;
		setService(include, clas, function);
	}
	/**
	 * sets Service that should be connected to
	 * @param include the file service
	 * @param clas the class service
	 * @param function the function service 
	 */
	public void setService(String include, String clas, String function){
		service = IOGate.SYSTEM.createObject();
		service.put(INCLUDE, include);
		service.put(CLASS, clas);
		service.put(FUNCTION, function);
	}
	public void setFunction(String function){
		service.remove(FUNCTION);
		service.put(FUNCTION, function);
	}
	/**
	 * sets the gate that should connect to
	 * @param g : IOGate server
	 */
	public void setGate(IOGate g){
		this.gate = g;
	}
	/**
	 * gets the gate that connected to now
	 * @return IOGate gate
	 */
	public IOGate getGate(){
		return gate;
	}
	/**
	 * returns service file 
	 * @return String service
	 */
	public String getInclude(){
		return service.getString(INCLUDE);
	}
	/**
	 * returns Class if any of the service
	 * @return String class
	 */
	public String getClas(){
		return service.getString(CLASS);
	}
	/**
	 * returns function of the service 
	 * @return String server function 
	 */
	public String getFunction(){
		return service.getString(FUNCTION);
	}
	/**
	 * wraps post of the IOGate
	 * @param data
	 * @param iores
	 * @see IOGate#post(JsonObject, IOResponce)
	 */
	public void post(JsonObject data, IOResponce iores){
		gate.post(createMessage(data), iores);
	}
	/**
	 * wraps get of the IOGate
	 * @param data
	 * @param iores
	 * @see IOGate#get(JsonObject, IOResponce)
	 */	
	public void get(JsonObject data, IOResponce iores){
		gate.get(createMessage(data), iores);
	}
	/**
	 * wraps securePost of the IOGate
	 * @param data
	 * @param iores
	 * @see IOGate#securePost(JsonObject, IOResponce)
	 */	
	public void securePost(JsonObject data, IOResponce iores){
		gate.securePost(createMessage(data), iores);
	}
	/**
	 * wraps secureGet of the IOGate
	 * @param data
	 * @param iores
	 * @see IOGate#secureGet(JsonObject, IOResponce)
	 */	
	public void secureGet(JsonObject data, IOResponce iores){
		gate.secureGet(createMessage(data), iores);
	}
	//This method copies header and appends Data to it  
	public JsonObject createMessage(JsonObject data){
	    JsonObject object = (JsonObject) service.copy();
	    object.put(DATA, data);
		return object;
	}
}
