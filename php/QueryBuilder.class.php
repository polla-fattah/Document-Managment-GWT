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
 * This class extractes sql statement from request data, the queries may be direct or indirect
 * for direct queries there is nothing to do just pass it to {@Link Gdbc} but if its indirect then
 * it findes saved sql sekilton and replaces all place holders with there relative complementary parts
 * in its order also it checks for sql injections, hence using inderect queries are recommended
 *
 * The place holders are like that
 * 1- IDENTIFIRE = "#idf;" // single identifier {field name and table name}
 * 2- IDENTIFIRES = "#ids;"// multiple identifiers
 * 3- VALUE = "#vlu;" // Single value
 * 4- VALUES = "#vls;"//multiple value
 * 5- EQUATION = "#equ;" // single equation in form of {field operator value}
 * 6- EQUATIONS = "#eqs;"// multiple equations
 */
class QueryBuilder{

	private $database; // information and specification about connecting database


	/** direct key for json object*/
	const DIRECT = "direct";
	/** indirect key for json object*/
	const INDIRECT = "indirect";
	/** method key for json object*/
	const METHOD = "method";

	/** Place holder for single identifier */
	const IDENTIFIRE = "#idf;";
	/** Place holder for multi identifiers */
	const IDENTIFIRES = "#ids;";
	/** Place holder for single value */
	const VALUE = "#vlu;";
	/** Place holder for multi values */
	const VALUES = "#vls;";
	/** Place holder for single equation */
	const EQUATION = "#equ;";
	/** Place holder for multi equations */
	const EQUATIONS = "#eqs;";

	/** AND operator for indirect queries that need equations*/
	const AND_OPERATOR = "#and;";
	/** OR operator for indirect queries that need equations*/
	const OR_OPERATOR = "#orr;";
	/** NOT operator for indirect queries that need equations*/
	const NOT_OPERATOR = "#not;";

	/** = operator for indirect queries that need equations*/
	const EQUAL = "#eql;";
	/** <= operator for indirect queries that need equations*/
	const LESSTHAN_OR_EQUAL = "#lte;";
	/** >= operator for indirect queries that need equations*/
	const GREATORTHAN_OR_EQUAL = "#gte;";
	/** >< operator for indirect queries that need equations*/
	const NOT_EQUAL = "#nql;";
	/** < operator for indirect queries that need equations*/
	const LESSTHAN = "#lth;";
	/** > operator for indirect queries that need equations*/
	const GREATORTHAN = "#gth;";

	/** ( operator for indirect queries that need equations*/
	const OPR = "#opr;";
	/** ) operator for indirect queries that need equations*/
	const CPR = "#cpr;";

	/**
	 * This constructor needs the database that connects to
	 *
	 * @param  $db  Database that is connected to
	 */
	function __construct($db){
		$this->database = $db;
	}

	/**
	 * For INDERECT queries there are six types of place holders
	 * 1- #fld; = single field
	 * 2- #fls; = group of fields
	 * 3- #vlu; = single value
	 * 4- #vls; = group of values
	 * 5- #equ; = single equation
	 * 6- #eqs; = group of equation
	 *
	 * This function substitutes each placeholder with its placement phrase after checking each
	 * placement for sql injection then returns a string with valid query
	 *
	 * for direct queries it just extracts sql statment from data and sends it directly to the {@Link Gdbc}
	 */
	function getQuery($data){

		$query = "";
		if ($data[self::METHOD] == self::DIRECT){
			if($stored->ALLOW_DIRECT_QUERY) {
				$query = $data[GDBC::SQL];
			}
			else {
				IOGate::reportError("Direct Queries are forbidden from the server.");
			}
		}
		elseif($data[self::METHOD] == self::INDIRECT){
			$stored= $this->database;
			$query = "";
			$receivedArr = explode("#col;", $data[GDBC::SQL]);
			$key = trim($receivedArr[0]);
			
			if(IOGate::$privacyLevel == "private" and array_key_exists($key, $stored->PRIVATE_GDBC_DBS)){
				$query = $stored->PRIVATE_GDBC_DBS[$key];
			}
			elseif(IOGate::$privacyLevel == "private" and array_key_exists($key, $stored->PUBLIC_GDBC_DBS)){
				$query = $stored->PUBLIC_GDBC_DBS[$key];
			}
			elseif(IOGate::$privacyLevel == "public" and array_key_exists($key, $stored->PUBLIC_GDBC_DBS)){
				$query = $stored->PUBLIC_GDBC_DBS[$key];
			}
			else {
						IOGate::reportError("Unknown Query Skilton index [$key].");
			}

			$loop = count($receivedArr);

			$index = 0;
			for ($i = 1; $i < $loop; $i++){
				$index = strpos($query, "#",$index);

				if ( $index === null or $index === "" or $index === false )
					break;
				if ($query[ $index + 4] !== ";"){
					$index++;
					continue;
				}

				$phrase = substr($query, $index, 5);

				if ($phrase === self::IDENTIFIRE){
					$query = substr_replace($query, $this->clearField($receivedArr[$i]), $index, 5);
				}
				elseif($phrase === self::IDENTIFIRES){
					$query = substr_replace($query, $this->clearFields($receivedArr[$i]), $index, 5);
				}
				elseif($phrase === self::VALUE){
					$query = substr_replace($query, $this->clearValue($receivedArr[$i]), $index, 5);
				}
				elseif($phrase === self::VALUES){
					$query = substr_replace($query, $this->clearValues($receivedArr[$i]), $index, 5);
				}
				elseif($phrase === self::EQUATION){
					$query = substr_replace($query, $this->clearEquation($receivedArr[$i]), $index, 5);
				}
				elseif($phrase === self::EQUATIONS){
					$query = substr_replace($query, $this->clearEquations($receivedArr[$i]), $index, 5);
				}
				$index +=4;

			}
		}

		return $query;
	}

	/*
	 * This function seperates and incomeing parameter to (field operator value ) then checks individually
	 * the sanity of each part
	 */
	private function clearEquation($e){
		$equation = "";
		$index = 0;

		while(true){
			$index = strpos($e, "#", $index);

			if ( $index === null or $index === "" or $index === false  )
				IOGate::reportError("Unknown Operator found near: ".substr($e, $index, 5));
			if ($e[$index + 4] !== ";"){
				$index+=4;
				continue;
			}

			$phrase = substr($e, $index, 5);

			if ($phrase === self::EQUAL){
				$arr = explode(self::EQUAL, $e);
				$equation  = $this->clearField($arr[0])  . " = " .$this->clearValue($arr[1]) ;
			}
			elseif($phrase === self::LESSTHAN_OR_EQUAL){
				$arr = explode(self::LESSTHAN_OR_EQUAL, $e);
				$equation  = $this->clearField($arr[0])  . " <= " .$this->clearValue($arr[1]) ;
			}
			elseif($phrase === self::GREATORTHAN_OR_EQUAL){
				$arr = explode(self::GREATORTHAN_OR_EQUAL, $e);
				$equation  = $this->clearField($arr[0])  . " >= " .$this->clearValue($arr[1]) ;
			}
			elseif($phrase === self::NOT_EQUAL){
				$arr = explode(self::NOT_EQUAL, $e);
				$equation  = $this->clearField($arr[0])  . " <> " .$this->clearValue($arr[1]) ;
			}
			elseif($phrase === self::LESSTHAN){
				$arr = explode(self::LESSTHAN, $e);
				$equation  = $this->clearField($arr[0])  . " < " .$this->clearValue($arr[1]) ;
			}
			elseif($phrase === self::GREATORTHAN){
				$arr = explode(self::GREATORTHAN, $e);

				$equation  = $this->clearField($arr[0])  . " > " .$this->clearValue($arr[1]) ;
			}

			return $equation;
		}
	}

	/*
	 * this function seperates defrent boolean statements by AND, OR, NOT, (, and ) then sends each of them
	 * to clearEquation function to check its sanity
	 */
	private function clearEquations($es){
		$operations = array(self::OPR => " ( ", self::CPR => " ) ", self::NOT_OPERATOR => " NOT ",
							self::AND_OPERATOR => " AND ", self::OR_OPERATOR => " OR ", );
		$rear = 0;
		$equations = "";
		$front = strpos($es, "#");
		$closeTest = true;
		$phrase = "";
		do{
			$phrase = substr($es, $front, 5);
			if(array_key_exists($phrase, $operations)){
				if (( $phrase == self::OR_OPERATOR || $phrase == self::AND_OPERATOR ||
					$phrase == self::CPR ) && $closeTest){
					$equations .= $this->clearEquation(substr($es,$rear, $front - $rear ));
				}

				$closeTest = ($phrase == self::CPR)? false: true;
				$equations .= $operations[$phrase];
				$rear = $front + 5;
			}
			$front = strpos($es, "#", $front + 5 );

		}while(!( $front == null or $front == "" or $front == false ));
		if ( $phrase == self::OR_OPERATOR || $phrase == self::AND_OPERATOR )
			$equations .= $this->clearEquation(substr($es,$rear));
		return $equations;

	}
	/*
	 * this function seperates fields by exploding String by #com; and then sends each field to clearField()
	 * to be checked then implod it by ,
	 */
	private function clearFields($fields){
		$fieldsArr =  explode("#com;", $fields);
		$len = count($fieldsArr);

		for($i = 0 ; $i < $len; $i++)
			$fieldsArr[$i] = $this->clearField($fieldsArr[$i]);
		$fields = implode(", ", $fieldsArr);

		return $fields;

	}
	/*
	 * This function checks for existing of `` quotes and sends the field to securityThreatRemoval($f)
	 */
	private function clearField($f){
		$f = trim($f);

		if ($f[0] == '`')
			$f = substr($f, 1);
		$len = strlen($f) - 1;
		if ($f[$len] == '`')
			$f = substr($f, 0, $len);
		$f = $this->securityThreatRemoval($f);
		return "`$f`";
	}
	/*
	 * this function seperates fields by exploding String by #com; and then sends each field to clearValue()
	 * to be checked then implod it by ,
	 */
	private function clearValues($values){
		//TODO add brackets (, and )  for ultiple value set
		$valuesArr =  explode("#com;", $values);
		$len = count($valuesArr);
	
		for($i = 0 ; $i < $len; $i++)
			$valuesArr[$i] = $this->clearValue($valuesArr[$i]);
		$values = implode(", ", $valuesArr);

		return $values;

	}
	/*
	 * This function checks for existing of '' quotes and sends the field to securityThreatRemoval($f)
	 */

	private function clearValue($v){
		$v = trim($v);

		if ($v[0] == "'")
			$v = substr($v, 1);
		$len = strlen($v) - 1;
		if ($v[$len] == "'")
			$v = substr($v, 0, $len);
		$v = $this->securityThreatRemoval($v);
		return "'$v'";
	}
	/*
	 * This function checks for security threats by replacing each ' with &#39;
	 * and then stripse anwonted HTML tags
	 */
	private function securityThreatRemoval($input){
		$input = str_replace( "'", "&#39;", $input);
		$input = strip_tags($input, $this->database->ALLOWED_TAGS);
		return $input;
	}



}
?>