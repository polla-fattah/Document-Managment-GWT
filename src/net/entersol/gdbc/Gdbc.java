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

import java.util.ArrayList;
import java.util.TreeMap;

import org.itemscript.core.values.JsonObject;
import org.itemscript.core.values.JsonValue;




import net.entersol.iogate.IOGate;
import net.entersol.iogate.IOGateException;
import net.entersol.iogate.IOResponce;
import net.entersol.iogate.IOService;
/**
 * <p>
 * This class is directly responsible of sending queries to the server via an {@link IOGate} and  
 * Receiving results from server as json object request object has this format
 * </p>
 * <pre>
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
 * </pre>
 * <p>Received object has this format</p>
 * <pre> 
 * {
 * 	db: String //database name,
 * 	index: integer // index of transaction,
 * 	total: integer // total number of statements,
 * 	queries:[
 * 				query1: { 
 * 						method : String // direct or indirect,
 * 						sql : {
 * 								results : [1st Row, 2nd Row],
 *  							lastInsertId : an integer or null,
 *  							rowCount: an integer
 * 							   }
 * 						},
 * 				query2: { 
 * 						method : String // direct or indirect,
 * 						sql : {
 * 								results : [1st Row, 2nd Row],
 *  							lastInsertId : an integer or null,
 *  							rowCount: an integer
 * 							   }
 * 						},
 * 				query#: { 
 * 						method : String // direct or indirect,
 * 						sql : {
 * 								results : [1st Row, 2nd Row],
 *  							lastInsertId : an integer or null,
 *  							rowCount: an integer
 * 							   }
  * 						}
 * 			]
 * }
 * </pre>
 */
public class Gdbc implements IOResponce {
	//These are keys to send values with to the server 
	private static final String DB = "db";			
	private static final String QUERIES = "queries";
	private static final String QUERY = "query";
	private static final String INDEX = "index";
	private static final String TOTAL = "total";
	
	/** Use this field if you use default database for the application*/
	public static final String DEFAULT_DB = "default";
	
	private static int currentIndex = 1;	// for identifying queries of one transaction
	private String connectedDb;				//The database that to be connected to

	private IOService ios;
	private ArrayList<SqlStatment> statements = new ArrayList<SqlStatment>(); //Array of sql statements
	// all transactions that has been sent and waiting responds from server
	private TreeMap<Integer, ArrayList<SqlStatment>> transactions = new  TreeMap<Integer, ArrayList<SqlStatment>> ();
	
	/**
	 * This constructor needs two arguments first the {@link IOGate} 
	 * for current database server and name of database 
	 * @param gate : The IOGate object that connects to server
	 * @param db : The database name
	 */
	public Gdbc(IOGate gate, String db){
		connectedDb = db;
		ios = new IOService(gate, "", "Gdbc", "queryListner");
	}
	/**
	 * adds sql statement to current transaction
	 * @param stmt
	 */
	public void register(SqlStatment stmt){
		statements.add(stmt);
	}
	/**
	 * clears all registered sql statements 
	 * @see Gdbc#register
	 */
	public void clear(){
		statements = new ArrayList<SqlStatment>();
	}
	/**
	 * This method submits registered statements and groups them as one transaction
	 */
	public void submit(){
		ArrayList<SqlStatment> currentStatments = statements;
		statements = new ArrayList<SqlStatment>();
		transactions.put(currentIndex , currentStatments);
				JsonObject message = IOGate.SYSTEM.createObject();
		JsonObject queries = IOGate.SYSTEM.createObject();
		
		int i = 0;
		for (;i < currentStatments.size();i++){
			queries.put(QUERY + i, currentStatments.get(i).getJsonStatment());
		}
		
		message.put(DB, connectedDb);
		message.put(INDEX, currentIndex);
		message.put(TOTAL, i);
		message.put(QUERIES, queries);
		
		currentIndex++;
		ios.post(message, this);
	}
	/**
	 * When one query fails in a transaction then all queries of that transaction 
	 *  triggers  {@link SqlStatment#onQueryFailure(SqlException)}
	 * @param ioge : IOGateException comse from {@link IOGate}
	 */
	@Override
	public void onCallBackFailure(IOGateException ioge) {
		//DoFuture change (currentIndex - 1)  to better index for failures 
		ArrayList<SqlStatment> stmts = transactions.get(currentIndex - 1);
		SqlException sqlExp = new SqlException(ioge.getMessage());
		sqlExp.setStatusText(ioge.getStatusText());
		sqlExp.setStatusCode(ioge.getStatusCode());
		for (int i = 0;i < stmts.size();i++){
			stmts.get(i).onQueryFailure(sqlExp);
		}
	}
	/**
	 * Response of all queries in a single transaction come here if all succeed then every {@link SqlStatment}
	 * object takes its result and triggers {@link SqlStatment#onQuerySuccess(ResultSet)}
	 * @param message : JsonValue an object passes from {@link IOGate}
	 */
	@Override 
	public void onCallBackSuccess(JsonValue message){
		//extracting data from message
		JsonObject obj = message.asObject();
		Integer index = obj.get(INDEX).asNumber().intValue();
		JsonObject results = obj.getObject(QUERIES);
		
		ArrayList<SqlStatment> stmts = transactions.get(index);
		
		for (int i = 0;i < stmts.size();i++){
			stmts.get(i).onQuerySuccess(new ResultSet(results.get(QUERY + i).asObject()));	
		}
	}
}
