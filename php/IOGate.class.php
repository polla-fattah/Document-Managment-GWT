<?php
/**
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
 * This class gives a standard gate to all services to communicate with GWT client it uses two variables for output and error
 * ot communicate over HTTP(S), if tere is error then sends it.
 * This class supports communication between client and server using JSON to communicate  data, status, and control messages
 * With this class get and post requests can be made.
 * This class supresses every output from any service just sends its own data, evry other services should use this
 * class as a gate to communicate data and error messages
 * Also its possible to have public or private messages which means by public is anonymous persons and with private
 * Authorized user by name and password.
 * It is also possible to encrypt data that should be sent across the communication but it is recommended to use
 * HTTPS if it is possible because it is faster, cleaner and more standardized
 *
 * @author Polla A. Fattah <br/><a href="mailto:polla@enterhosts.com">polla@enterhosts.com</a>
 */
class IOGate{
	private static $out;					//the outputs should come to this variable
	private static $err = "";				//the errors should come to this variable
	public static $securityLevel = null;	//secure or insecure
	public static $privacyLevel = null;	//private or public
	public static $userName = null;		//user UID if exist
	public static $password = null;		//user Password if exist
	static $appsdb;
	/**
	 * This constructor suppresses any output of other files
	 */
	function __construct(){
		if(System::PRODUCTION_MODE)
			ob_start();
		self::$appsdb = System::getAppDB();
	}
	/**
	 * This destructor flushes the buffer of output and then sends just the $out or $err variable if there is error
	 */
	function __destruct(){
		if(System::PRODUCTION_MODE)
			ob_end_clean();
		if(self::$err == ""){
			$message['type'] = "data";
			$message['context'] = self::$out;
			echo json_encode($message);
		}
		else{
			$message['type'] = "error";
			$message['context'] = self::$err;
			echo json_encode($message);
		}
	}

	/**
	 * This function creates a single interface to reporting error; it provides
	 * interface a way to log errors in a specified file (System::ERROR_LOG_FILE)
	 * then it terminates the script then the destructor is called
	 */
	public static function reportError($e){
		self::$err = $e;
		//logging errors
		if(System::ERROR_LOG_FILE != "")
			error_log("$e\r\n", 3, System::ERROR_LOG_FILE);
		//terminates the process after an error is reported
		die();
	}

	public static function send($m){
		self::$out = $m;
	}

	public static function sendln($m){
		self::$out .= "$m\n";
	}
	//This function strips the JSON string from strange behaviors
	private static function json_decode_nice($json, $assoc = false){
		$json = str_replace(array("\n","\r"),"",$json);
		$json = preg_replace('/([{,])(\s*)([^"]+?)\s*:/','$1"$3":',$json);
		$json = stripslashes($json);
		return json_decode($json,$assoc);
	}
	/**
	 * This function listens to the request and tests privacy level for this request acording to the
	 * privacy level also it takes security level
	 * @param $msg the message from client
	 * @param sl securityLevel
	 */
	public function listenToRequest($msg, $sl){
		$message = $this->json_decode_nice($msg, true);
		self::$securityLevel = $sl;
		self::$privacyLevel = $message["privacyLevel"];
		if(self::$privacyLevel == "private"){
			self::$userName = $message["userName"];
			self::$password = $message["password"];
			if($this->authenticate(self::$userName, self::$password) ){
				$this->processRequest($message["messageBody"]);
			}
			else{
				self::reportError("Authentication for '".self::$userName."' is Failed.");
			}
		}
		elseif(self::$privacyLevel == "public"){
				$this->processRequest($message["messageBody"]);
		}
		else{
			self::reportError("Unknown privacy level");
		}

	}
	//checks the user existence
	private function authenticate($user, $pass){
		$defaultDb = System::$defaultDatabase;
		$fail = false;
		$rows = self::$appsdb->query("select * from " . $defaultDb->AUTH_TABLE . " where idUsers='$user' and password = '$pass'") or $fail = true;
		if ($fail)
			IOGate::reportError("select * from " . $defaultDb->AUTH_TABLE . " where userId='$user' and password = '$pass'");
		if( $rows->rowCount() < 1 )
			return false;
		else
			return true;
	}
	//selects the required service
	private function processRequest($body){
		$called = true;
		$include = $body['include'];
		$class = $body['class'];
		$function = $body['function'];
		$data = $body['data'];
		$error = true;
		if($include != ""){
			require_once($include);
			if($function != "" && $class == ""){
				$function($data);
			}
			$error = false;
		}
		if($class != "" && $function != ""){

			$object = new $class;
			$object->$function($data);
			$error = false;
		}
		if($error){
			self::reportError("Service not selected");
		}
	}
}

?>
