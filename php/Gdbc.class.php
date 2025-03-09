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
 * This class is database servce that connects GWT requests to the PDO, then
 * it processes queries and sends database results to the clients
 *
 * The format of the json object is like this
 * {
 * 	db: String //database name,
 * 	index: integer // index of transaction,
 * 	total: integer // total number of statements,
 * 	queries:[
 * 				query1: {
 * 						method : String // direct or indirect,
 * 						sql : String // sql statment or index of the saved query at server
 * 						},
 * 				query2: {
 * 						method : String // direct or indirect,
 * 						sql : String // sql statment or index of the saved query at server
 * 						},
 * 				query#: {
 * 						method : String // direct or indirect,
 * 						sql : String // sql statment or index of the saved query at server
 * 						}
 * 			]
 * }
 *
 */
	class Gdbc{

		private $database;	// an object that contains information about database
		private $dbPointer; // resource pointer to the connected database

		/** index key for json object*/
		const INDEX = "index";
		/** queries key for json object*/
		const QUERIES = "queries";
		/** total key for json object*/
		const TOTAL = "total";
		/** query key for json object*/
		const QUERY = "query";
		/** type key for json object*/
		const TYPE = "type";
		/** sql key for json object*/
		const SQL =  "sql";
		/** db key for json object*/
		const DB = "db";
		/** default key for json object*/
		const DEFAULT_DB = "default";

		/*
		 * This function connects dbPointer to the given databse or connects it
		 * to the default databse of the main database of the application
		 */
		private function connect($data){
			if ($data[Gdbc::DB] == Gdbc::DEFAULT_DB){
				$this->database = System::$defaultDatabase;
				$this->dbPointer = IOGate::$appsdb;
			}
			else{
				$this->database = new $data[Gdbc::DB];
				try{
					$this->dbPointer = new PDO($this->database->DBMS . ":host=" . $this->database->DB_SERVER . ";dbname=" . $this->database->DB_NAME, $this->database->DB_USER, $this->database->DB_PASS);
				}
				catch(PDOException $e ){
					IOGate::reportError("Error while trying to connect to the database:\n" . $e->getMessage());
				}
			}
		}

		/**
		 * This function is the main function that listen to the user requests
		 */
		function queryListner($data){
			$this->connect($data);
			$queryBuilder = new QueryBuilder($this->database);

			$Queries = &$data[Gdbc::QUERIES];
			$this->dbPointer->beginTransaction();
			try{
				for($i = 0; $i < $data[Gdbc::TOTAL]; $i++){
					$Query = &$Queries[Gdbc::QUERY."$i"];
					$Query[Gdbc::SQL] = $this->query($queryBuilder->getQuery($Query));
				}
				$this->dbPointer->commit();
			}
			catch(PDOException $e){
				$this->dbPointer->rollBack();
				IOGate::reportError("Error While Executing queries:\n" . $e->getMessage());
			}
			catch(Exception $e){
				$this->dbPointer->rollBack();
				IOGate::reportError("Error While Executing queries:\n" . $e->getMessage());
			}
			IOGate::send($data);
		}

		/*
		 * This function extracts query and builds it then returns it as valid sql statement string
		 * by help of {@Link QueryBuilder}
		 */
		private function query($query){

			$result = Array();
			try{
				$test = false;
				$stmt = $this->dbPointer->query($query) or $test = true;
				if ($test){
					$errinf = $this->dbPointer->errorInfo();
					throw new Exception($errinf[2] .": ($query)");
				}
				$result['results'] = $stmt->fetchAll(PDO::FETCH_ASSOC);
				$result['rowCount'] = $stmt->rowCount();
				$result['lastInsertId'] = $this->dbPointer->lastInsertId();
			}
			catch(PDOException $pdoe){
				throw $pdoe;
			}
			catch(Exception $e){
				throw $e;
			}
			return $result;
		}

	}
?>