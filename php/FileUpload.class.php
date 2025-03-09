<?php

class FileUpload{
	static $appsdb;
    private static $GROUP = "group";
	private static $APPLICATION = "app";
	private static $PARENT_DIR = "parent";
	private static $THUMB_HIGHT = "th_h";
	private static $THUMB_WIDTH = "th_w";

	private $userId;
	private $groupId;
	private $appId;
	private $parentId;
	
	private $_fileDest;    //Direct file location on server

	private $_folderPath; //Include root dir

	private $_thumbWidth;
	private $_thumbHeight;
	private $_thumbPrefix;
	private $_thumbDest;

	private $_fileName;
	private $_fileTemp;
	private $_fileSize;
	private $_fileExtention;
	private $lastInseartredId;

	function uploadListner($data){
		self::$appsdb = System::getAppDB();
		$this->userId = empty(IOGate::$userName)  ? System::UNKNOWN_USER : IOGate::$userName;
		$this->groupId = $data[self::$GROUP];
		$this->appId = $data[self::$APPLICATION];
		$this->parentId = $data[self::$PARENT_DIR];
				
		$this->_fileName  = $this->getFileName($_FILES['file']['name']);
		$this->_fileTemp  = $_FILES['file']['tmp_name'];
		$this->_fileSize  = $_FILES['file']['size'];

		$rootDir   = System::UPLOAD_DIR == "" ? '../uploads/' : System::UPLOAD_DIR;
		do{
			$this->_folderPath = $rootDir . $this->appId . '/'. $this->userId . '/'. $this->randString(10) . '/';
		}while(is_dir($this->_folderPath));
		
		$this->_fileDest    = $this->_folderPath . $this->_fileName;
		
		$this->saveFile($data);
		
		IOGate::send($this->_fileName);
	}
	
	private function saveFile($data){
		$rows = $this->checkAutherization();
		$this->checkSizeLimits($rows);
		
		if($this->save2DB()){
			if(! $this->save2HD()){
				$this->foldBack();
				IOGate::reportError("Problem Occurred while writing file data to system file.");
			}
		}
		else{
			IOGate::reportError("Problem Occurred while writing file data to database.");
		}
		
		if(strtolower($this->_fileExtention) == "jpeg" || strtolower($this->_fileExtention) == "jpg"){
			$this->_thumbWidth  =  ($data[self::$THUMB_WIDTH] == "0" || $data[self::$THUMB_WIDTH] == "")? System::IMAGE_THUMB_WIDTH : $data[self::$THUMB_WIDTH];
			$this->_thumbHeight =  ($data[self::$THUMB_HIGHT] == "0" || $data[self::$THUMB_HIGHT] == "")? System::IMAGE_THUMB_HIGHT : $data[self::$THUMB_HIGHT];
			$this->_thumbPrefix =  System::IMAGE_THUMB_PREFIX == "" ? 'thumb_' : System::IMAGE_THUMB_PREFIX;
			$this->_thumbDest   =  $this->_folderPath . $this->_thumbPrefix . $this->_fileName;     // uploads/76sd7/thumb_798234.jpg
		
			$this->createImageThumb();
		}
	}
	
	private function save2HD(){
		return(mkdir($this->_folderPath, 0777, true) and move_uploaded_file($this->_fileTemp, $this->_fileDest));
	}
	private function save2DB(){
		$complete = true;


		self::$appsdb->query("INSERT INTO `File_File`(`idUsers`, `idGroup`, `idApplication`, `name`,
							`parentDir`, `realPath`, `size`, `type`) 
			VALUES (
						'$this->userId','$this->groupId','$this->appId','$this->_fileName','$this->parentId',
						'$this->_folderPath','$this->_fileSize','$this->_fileExtention');"
		) or $complete = false;
		$this->lastInseartredId = self::$appsdb->lastInsertId();
	
		if($complete){
			self::$appsdb->query("UPDATE `General_UsersAppGroup` SET `usedStorageSize`= `usedStorageSize` + $this->_fileSize
							WHERE `idGroup` = '$this->groupId' and `idApplication` = '$this->appId' and
							 `idUsers` = '$this->userId';") or $complete = false;
		
			if(!$complete)
				self::$appsdb->query("DELETE FROM `File_File` WHERE `idFile` = $this->lastInseartredId;");
		}
		return $complete;
	}
	
	private function getFileName($initialName){
		$fileArray = explode(".", basename($initialName));
		$baseName = $fileArray[0];
		$this->_fileExtention = end($fileArray);

		if(! $this->in_arrayi($this->_fileExtention, System::ALLOWED_EXTENSIONS))
			IOGate::reportError("File extension [ " . $this->_fileExtention . " ] is not suported.");
		
		foreach(explode(",", System::INVALID_CHARS) as $char)
			$baseName = str_replace($char, "_", $baseName);
		$baseName = str_replace(",", "_", $baseName);

		return $baseName . "." . $this->_fileExtention;
	}
    private function checkAutherization(){
		if($_FILES["file"]["error"] > 0)
			IOGate::reportError("Internal Server problem while trying to upload file");
		
		$rows = self::$appsdb->query("select * from `General_UsersAppGroup` where 
													`idUsers` = '$this->userId' and 
													`idApplication` = '$this->appId' and 
													`idGroup` = '$this->groupId' ;") 
                             or IOGate::reportError("Problem Occurred while reading user's data.");

		if( $rows->rowCount() < 1 )
			IOGate::reportError("The user [" . $this->userId . "] is not exist for this application.");
		return $rows;
	}
	private function checkSizeLimits($rows){
		$groupApp = $rows->fetch(PDO::FETCH_ASSOC);

        $limits = self::$appsdb->query("select * from `General_Group` where idGroup  = '$this->groupId';") 
                             or IOGate::reportError("Problem Occurred while reading user's data.");
		$limits = $limits->fetch(PDO::FETCH_ASSOC);
		

		if($this->_fileSize > $limits['maxFileLimit'])
			IOGate::reportError("File exceeded allowable  maximum file size");
		if(($groupApp['usedStorageSize'] + $this->_fileSize) > $limits['storageSizeLimit'])
			IOGate::reportError("No enough space to save this file");
	}
	
	private function in_arrayi($needle, $haystack) {
		return in_array(strtolower($needle),  explode(",", strtolower($haystack)));
	}

	public function randString($length = 5){
		$characters = "0123456789abcdefghijklmnopqrstuvwxyz";
		$string = "";
		$end = strlen($characters) - 1;
		for($x=0; $x<$length; $x++)
			$string.= $characters[mt_rand(1, $end)];

		return $string;
	}

	public function createImageThumb(){
		//create new image from file
		$img = imagecreatefromjpeg($this->_fileDest);

		//get image size
		$width = imagesx($img);
		$height = imagesy($img);
		$new_width = 0;
		$new_height = 0;
		// calculate thumbnail size
		if($width > $height){
			$new_width = $this->_thumbWidth;
			$new_height = floor($height * ($this->_thumbWidth/$width));
		}
		else{
			$new_height = $this->_thumbHeight;
			$new_width = floor($width * ($this->_thumbHeight / $height));
		}
		// create a new temporary image
		$tmp_img = imagecreatetruecolor((int)$new_width, (int)$new_height);

		// copy and resize old image into new image
		imagecopyresized($tmp_img, $img, 0, 0, 0, 0, $this->_thumbWidth,$this->_thumbHeight,$width,$height);

		// save thumbnail into a file
		imagejpeg($tmp_img, $this->_thumbDest);
	}


	public function foldBack() {
		self::$appsdb->query("DELETE FROM `File_File` WHERE `idFile` = $this->lastInseartredId;");
		self::$appsdb->query("UPDATE `General_UsersAppGroup` SET `usedStorageSize`= `usedStorageSize` - $this->_fileSize
			WHERE `idGroup` = '$this->groupId' and `idApplication` = '$this->appId' and `idUsers` = '$this->userId';");

	}
}


?>
