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
class DB1{

		public $DBMS = "mysql";							   //Type of database used

		public $DB_SERVER = "localhost";					//Path to the database server

		public $DB_USER = "root";							//databse user for this application

		public $DB_PASS = "";						    //password for user

		public $DB_NAME = "Future";					//The main database for this application

		public $AUTH_TABLE = "General_Users";		//The table that used for authenticating
														     //users for this Apps

		public $ALLOW_DIRECT_QUERY = false;   		//this is by default false because its highly unsecure
		
		/*
		 * The place holders are like that
		 * 1- "#idf;" => single identifier {field name and table name}
		 * 2- "#ids;" => multiple identifiers
		 * 3- "#vlu;" => Single value
		 * 4- "#vls;" => multiple value
		 * 5- "#equ;" => single equation in form of {field operator value}
		 * 6- "#eqs;" => multiple equations
		 */

		public $PRIVATE_GDBC_DBS = Array(						//This Array contains all queries for this database
			"General_Versioninig" => "Select * from `General_Versioninig` where `table` IN( #vls; )",
			"rt" => "Select * from Employee",
			"insertEmployee" => "insert into Employee (`name`, `age`, `employee`) values (#vls;)",
			"q3" => "Hi there"
		);//end of GDBC_DBS
		public $PUBLIC_GDBC_DBS = Array(						//This Array contains all queries for this database
			"q1" => "Select * from #idf; where #eqs;",
			"q2" => "insert into Staff (`name`,`degree`,Department_idDepartment) values ('Demo1', 'Msc', '1')",
			"q3" => "Hi there",
			"st" => "Select * from Student"
		);//end of GDBC_DBS

		public $ALLOWED_TAGS = "<br><i><u><b><center><h1><h2><h3><h4><h5><h6><hr>";
	}// end of class

?>
