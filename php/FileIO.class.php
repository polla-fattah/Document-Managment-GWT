<?php

class FileIO{
	static $appsdb;
	private $userId;
	private $userInfo;
	private $groupId;
	private $appId;
	
	private static $GROUP = "group";
	private static $APPLICATION = "app";
	private static $NEW_LABLE  = "newLable";
	private static $LABLE = "lable";
	private static $ID_FILE = "idFile";
	private static $PARENT_DIR = "parentDir";
	private static $NEW_NAME = "newName";
	private static $NEW_PARENT = "newParent";
	private static $STAR = "star";
	private static $LOCK = "lock";
	private static $DESCRIPTION = "desc";
	private static $NAME = "name";
	private static $SHARING_WITH = "sharingWith";
	private static $PHRASE = "phrase";
	private static $IMAGE = "image";

	//Basic file operations 
	public function ls($data){
		$this->initialize($data);
		
		$parentDir = $data[self::$PARENT_DIR];
		$ownerWhere = ($this->userInfo['storageMode'] == "owners") ? " " : " and f.`idUsers` = '$this->userId' " ;
		$query = "SELECT f.idFile, f.name, f.parentDir, f.size, f.type, f.starred, f.description, 
				GROUP_CONCAT( distinct l.idLable SEPARATOR ',') as lables, 
				GROUP_CONCAT( distinct s.sharedWith SEPARATOR ',') as sharedWith
			FROM File_File f left join File_Lable l on (f.idFile = l.idFile )
							 left join File_Sharing as s on (f.idFile = s.idFile)
			WHERE f.`parentDir` = '$parentDir' and  f.`idGroup` = '$this->groupId' and 
				  f.`idApplication` = '$this->appId' $ownerWhere  group by f.idFile";
		
		$files = self::$appsdb->query($query) 
			or 	IOGate::reportError("Problem occurred while reading directory information 1 $query");
		
		IOGate::send($files->fetchAll(PDO::FETCH_ASSOC));
	}
	
	function rm($data){
		$this->initialize($data);

		$fileIds = $data[self::$ID_FILE];
		natsort($fileIds);
		$fileIdsStr = implode(',', $fileIds);
		$ownerWhere = ($this->userInfo['storageMode'] == "owners") ? " " : " `idUsers` = '$this->userId'  and " ;

		$files = self::$appsdb->query(
			"select idFile, realPath, name, type from `File_File` where $ownerWhere  
				`idApplication` = '$this->appId' and `idGroup` = '$this->groupId' 
				and `idFile` in($fileIdsStr) ORDER BY idFile ASC;")
			or IOGate::reportError("Problem while reading file data");
		$files = $files->fetchAll(PDO::FETCH_ASSOC);
		$i=0;
		foreach ($files as $file){
			if( $fileIds[$i] != $file[self::$ID_FILE] )
				IOGate::reportError("Problem occurred while trying to delete  ($file[name]) ");
			$i++;
		}

		$this->deleteFiles($files);
		IOGate::send("Success");
	}
		
	public function rename($data){
		$this->initialize($data);

		$thumbPrefix =  System::IMAGE_THUMB_PREFIX == "" ? 'thumb_' : System::IMAGE_THUMB_PREFIX;

		$newName = $data[self::$NEW_NAME];
		foreach(explode(",", System::INVALID_CHARS) as $char)
			$newName = str_replace($char, "_", $newName);
		$newName = str_replace(",", "_", $newName);
				
		$files =  $this->getFilesInfo(implode(',', $data[self::$ID_FILE]), ($this->userInfo['storageMode'] == "owners"));
		$results = Array();
		for($i = 0; $i < count($files) ; $i++){
			$file = $files[$i];
			$baseName = ($i == 0)? "$newName" : "$newName$i"  ; 
			$tempName =  "$baseName.$file[type]"; 
			$newFullName = $file['realPath'] . $tempName;
			$oldFullName = $file['realPath'] . $file['name'];
			$currenFile = Array();
			$currenFile[self::$ID_FILE] = $file[self::$ID_FILE];
			$currenFile["newValue"] = $tempName;
			$results[] = $currenFile;

			if(strcmp($file['type'], "dir") == 0)
				System::getAppDB()->query("Update `File_File` set `name` = '$baseName' where idFile = '$file[idFile]';") 
						or IOGate::reportError("Database Error while renaming folder" );
			elseif(rename($oldFullName, $newFullName)){
				System::getAppDB()->query("Update `File_File` set `name` = '$tempName' where idFile = '$file[idFile]';") 
						or IOGate::reportError("Database Error while renaming file");
				
				if(strcmp($file['type'], "jpg") == 0 || strcmp($file['type'], "jpeg") == 0 )
					rename("$file[realPath]$thumbPrefix$file[name]", "$file[realPath]$thumbPrefix$tempName");
			}
			else
				IOGate::reportError("File System Error while renaming");
		}
		IOGate::send($results);
	}
	
	public function move($data){
		$this->initialize($data);

		$fileIds = $data[self::$ID_FILE];
		$fileIdsStr = implode(',', $fileIds);
		$ownerWhere = ($this->userInfo['storageMode'] == "owners") ? " " : " `idUsers` = '$this->userId'  and " ;

		$newParent = $data[self::$NEW_PARENT];
		// Check if its correct directory.
		$dir = self::$appsdb->query("select idFile from File_File where $ownerWhere `idApplication` = '$this->appId' and `idGroup` = '$this->groupId' and `idFile` = '$newParent' and `type` = 'dir' ;")
			or IOGate::reportError("Problem while reading Parent folder's data 1 ");
		if($dir->rowCount() < 1 and $newParent != "0")
			IOGate::reportError("Problem while reading Parent folder's data 2");
		//Check if they have same parent
		$dir = self::$appsdb->query("select DISTINCT parentDir from File_File where $ownerWhere `idApplication` = '$this->appId' and `idGroup` = '$this->groupId' and `idFile` in($fileIdsStr);")
			or IOGate::reportError("Problem while reading file Parent(s) data 1");
		if($dir->rowCount() != 1)
			IOGate::reportError("Problem while reading file Parent(s) data 2");
		
		//Check if there is recursive move a folder to itself
		$dir = self::$appsdb->query("select DISTINCT parentDir from File_File where `idFile` in($fileIdsStr) and `idFile` = '$newParent';")
			or IOGate::reportError("Can not move directory to itself 1");
		if($dir->rowCount() > 0)
			IOGate::reportError("Can not move directory to itself 2");
		
		System::getAppDB()->query("Update `File_File` set `parentDir` = '$newParent' where `idFile` in($fileIdsStr) ;") 
				or IOGate::reportError("Database Error while moving file(s)");
		IOGate::send("Success");
	}

	public function mkdir($data){
		$this->initialize($data);

		$parentDir = $data[self::$PARENT_DIR];
		$dirName = $data[self::$NAME];

		$limitCheck = self::$appsdb->query("Select * from General_UsersAppGroup natural join General_Group 
					where idUsers = '$this->userId' and (usedStorageSize + 4000) < storageSizeLimit;");
		
		
		if($limitCheck->rowCount() == 0)
			IOGate::reportError("No enough space to create a folder");
		System::getAppDB()->query("INSERT INTO `File_File`(`idUsers`, `idGroup`, `idApplication`, `name`,`parentDir`, `size`, `type`) 
							VALUES ('$this->userId','$this->groupId','$this->appId','$dirName','$parentDir','4000','dir');")
				or IOGate::reportError("Database Error while creating directory");
		$lastInsertID = System::getAppDB()->lastInsertId();
		System::getAppDB()->query("Update `General_UsersAppGroup` set usedStorageSize = usedStorageSize + 4000 
							Where `idApplication` = '$this->appId' and `idGroup` = '$this->groupId' and `idUsers` = '$this->userId' ;")
				or IOGate::reportError("Database Error while creating directory");
		
		$result = Array();
		$result['idFile'] = $lastInsertID;
		$result['name'] = $dirName;
		$result['parentDir'] = $parentDir;
		$result['size'] = "4000";
		$result['type'] = "dir";
		$result['starred'] = "0";
		$result['lables'] = "";
		$result['sharedWith'] = "";
		$results = Array(); //this is just because of the java
		$results[0] = $result;
		IOGate::send($results);
	}

	//Lables operations 
	public function addLable($data){
		$this->initialize($data);
		$values = "";
		$lables = $data[self::$LABLE];
		//check file existence 
		$fileIdsStr = implode(',', $data[self::$ID_FILE]);
		$files =  $this->getFilesInfo($fileIdsStr, ($this->userInfo['storageMode'] == "owners"));
		
		foreach($lables as $lable){
			$lable = $this->securityThreatRemoval($lable);
			foreach($files as $file)
				$values .= "('$lable','$file[idFile]','$this->groupId','$this->appId','$this->userId'),";
		}

		$values = substr($values, 0, -1);// remove the last comma ","
		
		self::$appsdb->query("INSERT IGNORE INTO `File_Lable` (`idLable`,`idFile`,`idGroup`,`idApplication`,`idUsers`) VALUES  $values ;") 
			or 	IOGate::reportError("Problem occurred while adding lable(s) to the File.");


		IOGate::send($this->sendMultiValueResult($data[self::$ID_FILE], "idLable", "File_Lable"));
	}
	
	public function removeLable($data){
		$this->initialize($data);

		$lables = $data[self::$LABLE];
		$strLables = "";
		foreach($lables as $lable){
			$lable = $this->securityThreatRemoval($lable);
			$strLables .= "'$lable',";
		}

		$strLables = substr($strLables, 0, -1);// remove the last comma ","

		$fileIdsStr = implode(',', $data[self::$ID_FILE]);
		$ownerWhere = ($this->userInfo['storageMode'] == "owners") ? " " : " `idUsers` = '$this->userId'  and " ;

		self::$appsdb->query("DELETE FROM `File_Lable` WHERE $ownerWhere `idLable` in ($strLables) and 
					`idFile` in ($fileIdsStr) and `idGroup` = '$this->groupId' and `idApplication` = '$this->appId' ;") 
			or 	IOGate::reportError("Problem occurred while removing lable from the File(s).");
		
		IOGate::send($this->sendMultiValueResult($data[self::$ID_FILE], "idLable", "File_Lable"));
	}
	
	public function renameLable($data){
		$this->initialize($data);

		$lable = $this->securityThreatRemoval($data[self::$LABLE]);
		$newLable = $this->securityThreatRemoval($data[self::$NEW_LABLE]);
		$ownerWhere = ($this->userInfo['storageMode'] == "owners") ? " " : " `idUsers` = '$this->userId'  and " ;
		
		self::$appsdb->query("UPDATE `File_Lable` SET `idLable`= '$newLable' WHERE $ownerWhere
					`idLable` = '$lable' and `idGroup` = '$this->groupId' and `idApplication` = '$this->appId' ;") 
			or 	IOGate::reportError("Problem occurred while renaming lable");
		
		$result = self::$appsdb->query("SELECT `idFile`, `idLable` from `File_Lable`  WHERE $ownerWhere
					`idLable` = '$newLable' and `idGroup` = '$this->groupId' and `idApplication` = '$this->appId' ;") 
			or 	IOGate::reportError("Problem occurred while renaming lable");
		
		IOGate::send($result->fetchAll(PDO::FETCH_ASSOC));
	}
	
	public function listLables($data){
		$this->initialize($data);

		$ownerWhere = ($this->userInfo['storageMode'] == "owners") ? " " : " `idUsers` = '$this->userId'  and " ;

		$lables = self::$appsdb->query("SELECT DISTINCT  `idLable` FROM `File_Lable` WHERE $ownerWhere `idGroup` = '$this->groupId' and `idApplication` = '$this->appId' ;") 
			or 	IOGate::reportError("Problem occurred while reading lable");
		$lables = $lables->fetchAll(PDO::FETCH_ASSOC);
		$lableArray = Array();
		foreach($lables as $lable)
			$lableArray[] = $lable['idLable'];
		IOGate::send($lableArray);
	}
	
	public function lls($data){
		$this->initialize($data);

		$ownerWhere = ($this->userInfo['storageMode'] == "owners") ? " " : " l.`idUsers` = '$this->userId' and " ;
		$ownerOn = ($this->userInfo['storageMode'] == "owners") ? " " : " f.idUsers = l.idUsers and " ;
		
		$lable = $this->securityThreatRemoval($data[self::$LABLE]);
		$query = "SELECT f.idFile, f.name, f.parentDir, f.size, f.type, f.starred, f.description, 
				GROUP_CONCAT( distinct l.idLable SEPARATOR ',') as lables, 
				GROUP_CONCAT( distinct s.sharedWith SEPARATOR ',') as sharedWith
				FROM `File_Lable` l left join File_File f On ($ownerOn f.idFile = l.idFile and f.idGroup = l.idGroup and f.idApplication = l.idApplication) 
						left join File_Sharing as s on (f.idFile = s.idFile)
				WHERE $ownerWhere l.`idLable` = '$lable' and l.`idGroup` = '$this->groupId' and l.`idApplication` = '$this->appId'  
				group by f.idFile;";
		$files = self::$appsdb->query($query) or IOGate::reportError("Problem occurred while reading lable [$query]");
		IOGate::send($files->fetchAll(PDO::FETCH_ASSOC));
	}
	
	//Lables operations 
	public function share($data){
		$this->initialize($data);
		if(strcasecmp($this->userInfo['storageMode'],"shareable")) // true it storageMod != sharable
			IOGate::reportError("This Application does not support sharing");
		$values = "";
		$sharingIDs = $data[self::$SHARING_WITH];
		//check file existence 
		$fileIdsStr = implode(',', $data[self::$ID_FILE]);
		$files =  $this->getFilesInfo($fileIdsStr, ($this->userInfo['storageMode'] == "owners"));
		foreach($sharingIDs as $sharingID){
			$sharingID = $this->securityThreatRemoval($sharingID);
			foreach($files as $file)
				$values .= "('$sharingID','$file[idFile]','$this->groupId','$this->appId','$this->userId'),";
		}

		$values = substr($values, 0, -1);// remove the last comma ","
		self::$appsdb->query("INSERT IGNORE INTO `File_Sharing` (`sharedWith`,`idFile`,`idGroup`,`idApplication`,`owner`) VALUES  $values ;") 
			or 	IOGate::reportError("Problem occurred while sharing File(s).  INSERT IGNORE INTO  `File_Sharing` (`sharedWith`,`idFile`,`idGroup`,`idApplication`,`owner`) VALUES  $values ;");

		IOGate::send($this->sendMultiValueResult($data[self::$ID_FILE], "sharedWith", "File_Sharing"));
	}
	
	public function unShare($data){
		$this->initialize($data);
		if(strcasecmp($this->userInfo['storageMode'],"shareable"))
			IOGate::reportError("This Application does not support sharing");

		$sharingIDs = $data[self::$SHARING_WITH];
		$strSharingIDs = "";
		foreach($sharingIDs as $sharingID){
			$sharingID = $this->securityThreatRemoval($sharingID);
			$strSharingIDs .= "'$sharingID',";
		}

		$strSharingIDs = substr($strSharingIDs, 0, -1);// remove the last comma ","

		$fileIdsStr = implode(',', $data[self::$ID_FILE]);
		
		self::$appsdb->query("DELETE FROM `File_Sharing` WHERE `owner` = '$this->userId'  and `sharedWith` in ($strSharingIDs) and 
					`idFile` in ($fileIdsStr) and `idGroup` = '$this->groupId' and `idApplication` = '$this->appId' ;") 
			or 	IOGate::reportError("Problem occurred while unsharing from the File(s).");
		
		$send = $this->sendMultiValueResult($data[self::$ID_FILE], "sharedWith", "File_Sharing");
		
		IOGate::send($send);

	}
	public function rms($data){
		$this->initialize($data);
		if(strcasecmp($this->userInfo['storageMode'],"shareable"))
			IOGate::reportError("This Application does not support sharing");
		$fileIdsStr = implode(',', $data[self::$ID_FILE]);
		self::$appsdb->query("DELETE FROM `File_Sharing` WHERE `sharedWith` = '$this->userId' and 
					`idFile` in ($fileIdsStr) and `idGroup` = '$this->groupId' and `idApplication` = '$this->appId' ;") 
			or 	IOGate::reportError("Problem occurred while removing shared file(s).");
		IOGate::send("Success");
	}

	public function sls($data){
		$this->initialize($data);
		$files = "";
		if(strcasecmp($this->userInfo['storageMode'],"shared") == 0){
			$query = "SELECT idFile, f.name,u.name as ownerName, f.idUsers as ownerId,  parentDir, size, type, starred, description
					FROM `General_Users` u inner join `File_File` f on ( u.idUsers = f.idUsers ) WHERE  `idGroup` = '$this->groupId' and `idApplication` = '$this->appId'  ;";
			$files = self::$appsdb->query($query) or IOGate::reportError("Problem occurred while reading shared file(s) [$query]");			
		}
		elseif (strcasecmp($this->userInfo['storageMode'],"shareable") == 0){
			$query = "SELECT f.idFile, f.name, u.name as ownerName, f.idUsers as ownerId, f.parentDir, f.size, f.type, f.starred,  f.description
					FROM  File_Sharing s natural join File_File f inner join `General_Users` u    on ( u.idUsers = f.idUsers )
					WHERE s.`idGroup` = '$this->groupId' and f.`idApplication` = '$this->appId' and sharedWith = '$this->userId';";
	//		IOGate::reportError("$query");

			$files = self::$appsdb->query($query) or IOGate::reportError("Problem occurred while reading shared files {$query}");
		}
		else{
			IOGate::reportError("This Application does not support shared file(s)");
		}
		IOGate::send($files->fetchAll(PDO::FETCH_ASSOC));
	}
	
	public function star($data){
		$this->initialize($data);

		$fileIds = $data[self::$ID_FILE];
		$fileIdsStr = implode(',', $fileIds);
		$ownerWhere = ($this->userInfo['storageMode'] == "owners") ? " " : " `idUsers` = '$this->userId'  and " ;

		$star = $data[self::$STAR];

		System::getAppDB()->query("Update `File_File` set `starred` = '$star' where $ownerWhere
				 `idApplication` = '$this->appId' and `idGroup` = '$this->groupId' 
				and `idFile` in($fileIdsStr) ;") 
				or IOGate::reportError("Database Error while moving file(s)");
	
		$send = $this->sendMultiValueResult($data[self::$ID_FILE], "starred", "File_File");
		
		IOGate::send($send);	
	}
	
	public function editDescription($data){
		$this->initialize($data);

		$fileIds = $data[self::$ID_FILE];
		$ownerWhere = ($this->userInfo['storageMode'] == "owners") ? " " : " `idUsers` = '$this->userId'  and " ;

		$description = $this->securityThreatRemoval($data[self::$DESCRIPTION]);
		
		System::getAppDB()->query("Update `File_File` set `description` = '$description' where $ownerWhere
				 `idApplication` = '$this->appId' and `idGroup` = '$this->groupId' 
				and `idFile` = $fileIds ;") 
				or IOGate::reportError("Database Error while moving file(s)");
		IOGate::send("Success");
	}

	public function find($data){
		$this->initialize($data);
		
		$phrase = $this->securityThreatRemoval($data[self::$PHRASE]);
		$ownerWhere = ($this->userInfo['storageMode'] == "owners") ? " " : " and `idUsers` = '$this->userId' " ;
		$files = self::$appsdb->query("
				SELECT idFile, name, parentDir, size, type, starred,  description 
					FROM File_File	WHERE ( `name` like '%$phrase%'  or `description` like '%$phrase%' ) and 
					`idGroup` = '$this->groupId' and `idApplication` = '$this->appId' $ownerWhere ;") 
			or 	IOGate::reportError("Problem occurred while reading directory information 2");
		IOGate::send($files->fetchAll(PDO::FETCH_ASSOC));
	}
	
	public function findShare($data){
		$this->initialize($data);
		if(strcasecmp($this->userInfo['storageMode'],"owners") == 0 || strcasecmp($this->userInfo['storageMode'],"unshareable") == 0 )
			IOGate::reportError("This Application does not support sharing");

		$files = "";
		$phrase = $this->securityThreatRemoval($data[self::$PHRASE]);

		if(strcasecmp($this->userInfo['storageMode'],"shared") == 0){
			$files = self::$appsdb->query("SELECT  idFile, name, parentDir, size, type, starred,  description FROM `File_Sharing`  WHERE  `idGroup` = '$this->groupId' and `idApplication` = '$this->appId'  and ( `name` like '%$phrase%'  or `description` like '%$phrase%' );") 
				or 	IOGate::reportError("Problem occurred while reading shared file(s)  ");			
		}
		elseif (strcasecmp($this->userInfo['storageMode'],"shareable") == 0){
			$sharingWith = $this->securityThreatRemoval($data[self::$SHARING_WITH]);
			$files = self::$appsdb->query("SELECT f.idFile, f.name, f.parentDir, f.size, f.type, f.starred, f.description
					FROM `File_Sharing` s inner join File_File f On (f.idFile = s.idFile and f.idGroup = s.idGroup and f.idApplication = l.idApplication) 
					WHERE (s.`owner` = '$this->userId' and s.`sharedWith` = '$sharingWith' and s.`idGroup` = '$this->groupId' and s.`idApplication` = '$this->appId')
					and ( f.`name` like '%$phrase%'  or f.`description` like '%$phrase%' );") 
				or 	IOGate::reportError("Problem occurred while reading ");
		}
		else{
			IOGate::reportError("This Application does not support sharing shared file(s)");
		}
		IOGate::send($files->fetchAll(PDO::FETCH_ASSOC));
	}

	public function downloadFile($data){
		$this->initialize($data);
		$file = $this->getFilesInfo($data[self::$ID_FILE]);

		$fname = $file[0]['name'];
		$file = $file[0]['realPath'].$fname;

		header("Content-Disposition: attachment; filename=$fname");
		header("Content-Type: application/force-download");
		header("Content-Length: " . filesize($file));
		header("Connection: close");
		readfile($file);
	}
	
	public function downloadImage($data){
		$this->initialize($data);
		$file = $this->getFilesInfo($data[self::$ID_FILE]);
		$file = $file[0];

		if(strcasecmp($file['type'],"jpg") != 0)
			IOGate::reportError("Just jpg file type supported as image file");

		$fname = $file['name'];
		if(strcasecmp($data[self::$IMAGE],"thumb") == 0){
			$thumbPrefix =  System::IMAGE_THUMB_PREFIX == "" ? 'thumb_' : System::IMAGE_THUMB_PREFIX;
			$fname = $thumbPrefix . $fname; 
		}
		$fullFile = $file['realPath'].$fname;

		$file['name'] = $fname;
		
		$imageFile = file_get_contents($fullFile) ;
		
		$file[self::$IMAGE] = base64_encode($imageFile);
		IOGate::send($file);
	}
	
	public function listGroupMembers($data){
		$this->initialize($data);
		$users = self::$appsdb->query("	SELECT u.idUsers as idUsers, u.name as name
					FROM General_UsersAppGroup ua join	General_Users u on (ua.idUsers = u.idUsers)
				WHERE ua.`idGroup` = '$this->groupId' and `idApplication` = '$this->appId'  ;") 
			or 	IOGate::reportError("Problem occurred while reading users list");
		IOGate::send($users->fetchAll(PDO::FETCH_ASSOC));

	}
	
	//private Helper function
	private function rrmdir($dir) {
		if (is_dir($dir)) {
			$objects = scandir($dir);
			foreach ($objects as $object) {
				if ($object != "." && $object != "..") {
					if (filetype($dir."/".$object) == "dir") rrmdir($dir."/".$object); 
					else unlink($dir."/".$object);
				}
			}
			reset($objects);
			rmdir($dir);
		}
	}
	
	private function securityThreatRemoval($input){
		$input = strtr($input, "'", "&#39;");
		$input = strip_tags($input, "<br><i><u><b><center><h1><h2><h3><h4><h5><h6><hr>");
		return $input;
	}
	
	private function deleteFiles($files){
		$i = 0;
		$j = 0;
		while(true){
			$parents = Array();
			for($j = 0 ;($i + $j) < count($files);$j++){
				$file = $files[($i + $j)];
				if(strcasecmp($file['type'], "dir") == 0)
					$parents[$j] =  $file['idFile'];
			}
			$tt = count($parents);
			if($tt == 0 )
				break;
			
			$i += $j;
			$parentsStr = implode(',', $parents);

			$subFiles = self::$appsdb->query("select idFile, realPath, name, type from `File_File` where  `parentDir` in
					($parentsStr);")
				or IOGate::reportError("Error while readinf file info");
			
			$subFiles = $subFiles->fetchAll(PDO::FETCH_ASSOC);
			$files = array_merge($files , $subFiles);
		}

		foreach($files as $file )
			if(!file_exists($file['realPath']) && strcasecmp($file['type'], "dir") != 0)
				IOGate::reportError("Problem occurred while deleting ($file[name])");
			
		$idFiles = Array();
		foreach($files as $file ){
			$idFiles[] = $file['idFile'];
			if(strcasecmp($file['type'], "dir") != 0)
				$this->rrmdir($file['realPath']);
		}
		$fileIds = implode(',', $idFiles);
		self::$appsdb->exec("Delete from File_File where idFile in ( $fileIds ) ;") 
				or IOGate::reportError("Database Error while deleting file(s).");

	}
	
	private function getFilesInfo($id, $ownerMode = false){
		$file = "";
		$owner = $ownerMode ? " " : " `idUsers` = '$this->userId' and ";
		$file = self::$appsdb->query("Select  * From File_File where `idFile` in  ($id) and $owner
					`idApplication` = '$this->appId' and `idGroup` = '$this->groupId' ORDER BY idFile ASC;") 
					or IOGate::reportError("Problem Occurred while reading File data.");

		if($file->rowCount() < 1)
			IOGate::reportError("File is not exist.");
		
		return $file->fetchAll(PDO::FETCH_ASSOC);
	}
	
	private function initialize($data){
		self::$appsdb = IOGate::$appsdb;
		$this->userId = empty(IOGate::$userName)  ? System::UNKNOWN_USER : IOGate::$userName;
		$this->groupId = $data[self::$GROUP];
		$this->appId = $data[self::$APPLICATION];
		$rows = self::$appsdb->query(
				"select * from `General_UsersAppGroup` where 
								`idUsers` = '$this->userId' and 
								`idApplication` = '$this->appId' and 
								`idGroup` = '$this->groupId' ;")
			or IOGate::reportError("Problem Occurred while reading user's data.");

		if( $rows->rowCount() < 1 )
			IOGate::reportError("The user [" . $this->userId . "] is not exist for this application.");
		$this->userInfo =  $rows->fetch(PDO::FETCH_ASSOC);
		$rows = self::$appsdb->query("select storageMode from `General_Application` where `idApplication` = '$this->appId';")
			or IOGate::reportError("Problem Occurred while reading Application data.");
		$rows =  $rows->fetch(PDO::FETCH_ASSOC);
		$this->userInfo['storageMode'] = $rows['storageMode'];
	}
	
	private function sendMultiValueResult($idFiles, $groupConcat, $table){
		$strIdFiles = implode(',', $idFiles);
		$query = "SELECT idFile, GROUP_CONCAT( distinct $groupConcat SEPARATOR ',') as newValue 
			FROM $table
			WHERE idFile in ($strIdFiles) and `idGroup` = '$this->groupId' and `idApplication` = '$this->appId'  
			group by idFile";
		
		$result = self::$appsdb->query($query) 
			or 	IOGate::reportError("Problem occurred while reading directory information 1 $query");
		
		$result = $result->fetchAll(PDO::FETCH_ASSOC);

		
		foreach( $result as $obj){
			if (($index = array_search($obj["idFile"], $idFiles)) !== false)
				unset($idFiles[$index]);
		}
		foreach($idFiles as $fid){
			$object = new StdClass;
			$object->idFile = $fid;
			$object->newValue = "";
			$result[] = $object;
		}

		return $result;

	}
	
}

?>
