<?php
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
/**
 * This file is the only way that requests can come from and here is decided how to
 * send the request to the IOGate class
 */

/*
 * these headers for preventing cache servers to cache any data, because the requests
 * that made needs real instan answers otherwise there will be no request because GWT client have the old data
 */
	header('Content-Type: text/javascript');
	header('Cache-Control: no-cache');
	header('Pragma: no-cache');

	ini_set('display_errors', 1);
	ini_set('error_reporting', E_ALL);

	$called = true; //to prevent any request to other files each service file
						 // (excluding java like class files) should check existance of this variable
	include_once("settings.php");

	$iogate = new IOGate;	//the only gate for all services for consistency and security

	//This multiway if else statment checks the data comes from secure or insecure stream
	if(isset($_REQUEST['insecure'])){
		$iogate->listenToRequest($_REQUEST['insecure'], "insecure");
	}
	elseif(isset($_REQUEST['secure']) ){
		//decrept data befor hand it over to iogate
		$iogate->listenToRequest(Security::encrypt($_REQUEST['secure']), "secure");
	}
	else{
	//illegal requests
		IOGate::reportError("Illegal request");
	}
?>
