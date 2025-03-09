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

import net.entersol.iogate.IOGate;

import org.itemscript.core.values.JsonObject;
/**
 * <p>
 * This class creates sql statements that is ready to be sent by {@link Gdbc} the queries are two types:
 * direct and indirect direct query is simple, it just send plain sql from client to server,
 * indirect queries are queries saved in the server as array the client just send index and complementary arguments
 * </P>
 * <p>
 * The format of the sending string is like that "index#col; [complementary#col;]*"  
 * while complementary can be </p>
 * <ol>
 * <li> single field; example  name
 * <li> multi fields;  example name1#com; name2#com; name3
 * <li> single value; example value
 * <li> multi values; value#com; 'value'#com; value
 * <li> single equation; example 1) name #eql; value 2) name #gte; 'value'
 * <li> multi equations; example 1) `name` #nql; value #and; `name2` #nql; 'value1'  
 * 		2) field #gth; 'val' #and; #not; #opr; field #gte; 33 #cpr;
 * </ol>
 * <p>
 * for using these operators you can use available public fields of this class
 *  they should be in this format for security reasons
 *  </p>
 *  <p>
 *  This class sends an object of Json to the {@link Gdbc} in this format 
 *  </p>
 *  <pre>
 *  {
 *  	method: string , //direct or indirect
 *  	sql: string // the sql statement 
 *  }
 *  </pre>
 */
public abstract class SqlStatment {
	/** This mode directly insert sql statements from client	 */
	public static final String DIRECT = "direct";
	/** This mode just send index of the stored sql statement at the server side	 */
	public static final String INDIRECT = "indirect";
	
	/** Logical AND operator for indirect queries that need equations*/
	public static final String AND = "#and;";
	/** Logical OR operator for indirect queries that need equations*/
	public static final String OR = "#orr;";
	/** Logical NOT operator for indirect queries that need equations*/
	public static final String NOT = "#not;";
	/** = operator for indirect queries that need equations*/
	public static final String EQUAL = "#eql;";
	/** <= operator for indirect queries that need equations*/
	public static final String LESSTHAN_OR_EQUAL = "#lte;";
	/** >= operator for indirect queries that need equations*/
	public static final String GREATORTHAN_OR_EQUAL = "#gte;";
	/** <> operator for indirect queries that need equations*/
	public static final String NOT_EQUAL = "#nql;";
	/** < operator for indirect queries that need equations*/
	public static final String LESSTHAN = "#lth;";
	/** > operator for indirect queries that need equations*/
	public static final String GREATORTHAN = "#gth;";
	/** ( for indirect queries that need equations*/
	public static final String OPR = "#opr;";
	/** ) for indirect queries that need equations*/
	public static final String CPR = "#cpr;";
	/** : for indirect queries that need equations*/
	public static final String COLON = "#col;";
	/** , for indirect queries that need equations*/
	public static final String COMMA = "#com;";
	
	protected String query = null;		//The query should be sent
	private String method = DIRECT;		//method of query substitution
	
	private static final String SQL = "sql";		//sql as key for json object that has to be sent 
	private static final String METHOD = "method";	//method as key for json object that has to be sent 
	
	/**
	 * Default constructor do nothing
	 */
	public SqlStatment(){
	}
	/**
	 * This constructor can specify method of sending query namely  DIRECT or INDERECT please use predefined 
	 * final strings for that {@link SqlStatment#DIRECT} or {@link SqlStatment#INDIRECT}
	 * @param m : String that tells which method DIRECT or INDERECT
	 */
	public SqlStatment(String m){
		setMethod(m);
	}
	/**
	 * This constructor can specify method of sending query namely  DIRECT or INDERECT please use predefined 
	 * final strings for that {@link SqlStatment#DIRECT} or {@link SqlStatment#INDIRECT}. also it specifies the query that should be sent
	 * @param m String that tells which method DIRECT or INDERECT
	 * @param q String of the query 
	 */
	public SqlStatment(String m, String q){
		query = q;
		setMethod(m);
	}
	/**
	 * This method sets method of sending query namely  DIRECT or INDERECT please use predefined 
	 * final strings for that {@link SqlStatment#DIRECT} or {@link SqlStatment#INDIRECT}. also it specifies the query that should be sent
	 * @param m : String that tells which method DIRECT or INDERECT
	 */
	public void setMethod(String m){
		if(method != DIRECT && method != INDIRECT)
			throw new IllegalArgumentException("Use final variables DIRECT or INDIRECT");
		method = m;
 	}
	/**
	 * returns sending quiries method
	 * @return String DIRECT or INDERECT
	 */
	public String getMethod(){
		return method;
	}
	/**
	 * sets query that should be sent
	 * @param currentQuery
	 */
	public void setQuery(String currentQuery) {
		query = currentQuery;
	}
	/**
	 * returns query of this statement or null if query is not set 
	 * @return String query
	 */
	public String getQuery(){
		return query;
	}
	/**
	 * this method returns the Json object that represents this statement to be sent to the server as json  
	 * @return statement in json object
	 */
	public JsonObject getJsonStatment(){
		JsonObject stmt = IOGate.SYSTEM.createObject();
		stmt.p(METHOD, method);
		stmt.p(SQL, query);
		return stmt;
	}

	/**
	 * Called when service returned successfully
	 * This method will called if successful page with return 200 comes and without any programming error
	 * in the server service that is called.
	 * 
	 * @param result : The message that should be send to the service
	 */
	public abstract void onQuerySuccess(ResultSet result);
	
	/**
	 * Called when the service failed to return successfully
	 * This method is called if there is a problem occurred to the service; problems like:
	 * <ol>
	 *  <li> disconnection of network
	 *  <li> wrong service call
	 *  <li> unauthorized call
	 *  <li> problems inside service itself
	 *  </ol>
	 * @param sqlex : is an exception that provided by the connecter class to the caller class to identify problem type
	 */
	public abstract void onQueryFailure(SqlException sqlex);
}
