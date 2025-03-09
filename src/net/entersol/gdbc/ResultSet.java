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

package net.entersol.gdbc;

import org.itemscript.core.values.JsonArray;
import org.itemscript.core.values.JsonObject;
/**
 * <p>
 * The respond from the PHP server for SQL queries are passed to this class it 
 * contains results for all kind of Queries but it behaves differently for queries
 *  that suppose to retrieve data and others that manipulate it.  
 *  </P>
 *  <p>
 *  The JSON format that expected for this class is exactly Like this:
 *  <p>
 *  <pre> 
 *   sql:{
 *  	results : [1st Row, 2nd Row],
 *  	lastInsertId : an integer or null,
 *  	rowCount: an integer
 *	  }
 *	<pre>
 */
public class ResultSet {
	private JsonObject jsonData;	// The data that comes from server
	private JsonArray results;		// Results of Select query, for manipulate query it will be empty
	private int rowCount;			// The rows that affected by this query
	private Integer lastInsertId;	// Just for insert queries it contain numbers other wise its null
	private int pointer;			// The pointer for traversing inside fetched results 
	private JsonObject row = null;	// The current row that pointer stopped on 
	private int size;				// The number of fetched rows
	
	/**
	 * This constructor takes an object of json and parsed it to its original components
	 * objects of this class are typically created by {@link Gdbc} as they 
	 * have the answer of queries from server and pass them to {@link SqlStatment#onQuerySuccess(ResultSet)} 
	 *
	 * @param jsonValue 
	 */
	public ResultSet(JsonObject jsonValue) {
		jsonData = jsonValue.getObject("sql");

		results = jsonData.getArray("results");

		lastInsertId = jsonData.getInt("lastInsertId");
		rowCount = jsonData.getInt("rowCount");

		size = results.size();
		pointer = 0;
	}
	
	/**
	 * This method needs a parameter, the name for field in the retrieved data it 
	 * returns the field of current row that the pointer points to.
	 * @see ResultSet#getRow()
	 * @param colName
	 * @return boolean value of the field
	 */
	public boolean getBoolean(String colName){
		if(!row.containsKey(colName))
			throw new IllegalArgumentException("Unknown Column Name");
		return row.getBoolean(colName);
	}
	
	/**
	 * This method needs a parameter, the name for field, in the retrieved data it 
	 * returns the field of current row that the pointer points to.
	 * @see ResultSet#getRow()
	 * @param colName
	 * @return double value of the field
	 */
	public double getDouble(String colName){
		if(!row.containsKey(colName))
			throw new IllegalArgumentException("Unknown Column Name");
		return row.getDouble(colName);
	}

	/**
	 * This method needs a parameter, the name for field, in the retrieved data it 
	 * returns the field of current row that the pointer points to.
	 * @see ResultSet#getRow()
	 * @param colName
	 * @return float value of the field
	 */
	public float getFloat(String colName){
		if(!row.containsKey(colName))
			throw new IllegalArgumentException("Unknown Column Name");
		return row.getFloat(colName);
	}

	/**
	 * This method needs a parameter, the name for field, in the retrieved data it 
	 * returns the field of current row that the pointer points to.
	 * @see ResultSet#getRow()
	 * @param colName
	 * @return int value of the field
	 */
	public int getInt(String colName){
		if(!row.containsKey(colName))
			throw new IllegalArgumentException("Unknown Column Name");
		return row.getInt(colName);
	}
	/**
	 * Returns the complete object that comes from server,  
	 * you can use this object to for customized retrieval inside fetched data
	 * @return JsonObject
	 */
	public JsonObject getJsonData(){
		return jsonData;
	}
	/**
	 * This method returns the value of auto incremented value of insert statement, 
	 * if there is no auto increment or the statement is not insert
	 * @return integer or null
	 */
	public Integer getLastInsertId(){
		return lastInsertId;
	}
	
	/**
	 * This method needs a parameter, the name for field, in the retrieved data it 
	 * returns the field of current row that the pointer points to.
	 * @see ResultSet#getRow()
	 * @param colName
	 * @return long value of the field
	 */	
	public long getLong(String colName){
		if(!row.containsKey(colName))
			throw new IllegalArgumentException("Unknown Column Name");
		return row.getLong(colName);
	}
	/**
	 * This method returns json array of all fetched results for select statment, 
	 * it returns an empty array if statement is not select.
	 *  
	 * @return JsonArray 
	 */
	public JsonArray getResults(){
		return results;
	}
	/**
	 * This method returns a json object of the current row that pointed to by the pointer
	 * @return JsonObject
	 */
	public JsonObject getRow(){
		return row;
	}
	/**
	 * This method returns number of affected rows by the quiry 
	 * @return int 
	 */
	public int getRowCount(){
		return rowCount;
	}
	
	/**
	 * This method needs a parameter, the name for field, in the retrieved data it 
	 * returns the field of current row that the pointer points to.
	 * @see ResultSet#getRow()
	 * @param colName
	 * @return String value of the field
	 */
	public String getString(String colName){
		if(!row.containsKey(colName))
			throw new IllegalArgumentException("Unknown Column Name");
		return row.getString(colName);
	}
	/**
	 * This function moves the pointer to the next row of the fetched data if it success returns true
	 * if not returns false, this indicates the end of the fields 
	 * @return boolean 
	 */
	public boolean next(){
		if (pointer < size){
			row = results.getObject(pointer);
			pointer++;
			return true;
		}
		return false;
	}
	
	/**
	 * This function moves the pointer to the previous row of the fetched data if it success returns true
	 * if not returns false, this indicates the beginning of the fields 
	 * @return boolean 
	 */
	public boolean previous(){
		if (pointer > 0){
			pointer--;
			row = results.getObject(pointer);
			return true;
		}
		return false;
	}
	
	/**
	 * This function moves the pointer to the first row of the fetched data if it success returns true
	 * if not returns false
	 * @return boolean
	 */
	public boolean first(){
		if (size > 0){
			pointer = 0;
			row = results.getObject(pointer);
			return true;
		}
		return false;
	}

	/**
	 * This function moves the pointer to the lasr row of the fetched data if it success returns true
	 * if not returns false
	 * @return boolean
	 */
	public boolean last(){
		if (size > 0){
			pointer = size - 1;
			row = results.getObject(pointer);
			return true;
		}
		return false;
	}
	/**
	 * This method checks if the pointer points to the first row of the fetched data or not
	 * @return boolean
	 */
	public boolean isFirst(){
		if (row == results.getObject(0))
			return true;
		return false;
		
	}

	/**
	 * This method checks if the pointer points to the end row of the fetched data or not
	 * @return boolean
	 */
	public boolean isLast(){
		if (row == results.getObject(size -1))
			return true;
		return false;
	}
}
