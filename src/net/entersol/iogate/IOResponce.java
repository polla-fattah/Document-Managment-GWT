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

import org.itemscript.core.values.JsonValue;
/**
 * Server response will goes to one of these functions if IOGate class is used to communicate with server then this interface should be implemented and an instant of it passes to the calling function.
 * Then according to the success or failure of the connection onCallBackSuccess or onCallBackFailure will called respectively
 * 
 * @author Polla A. Fattah <br/><a href="mailto:polla@enterhosts.com">polla@enterhosts.com</a>
 */
public interface IOResponce {
	/**
	 * Called when service returned successfully
	 * This method will called if successful page with return 200 comes and without any programming error
	 * in the server service that is called. 
	 * @param message The message that should be send to the service
	 */

	public void onCallBackSuccess(JsonValue message);
	/**
	 * Called when the service failed to return successfully<br>
	 * This method is called if there is a problem occurred to the service; problems like:
	 * <ol>
	 *  <li> disconnection of network
	 *  <li> wrong service call
	 *  <li> unauthorized call
	 *  <li> problems inside service itself
	 *  </ol>
	 * @param ioge is an exception that provided by the connecter class to the caller class to identify problem type
	 */
	public void onCallBackFailure(IOGateException ioge);
}
