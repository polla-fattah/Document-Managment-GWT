package net.entersol.indexeddb;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
//import java.util.Vector;
import net.entersol.iogate.IOGate;

import org.itemscript.core.values.JsonArray;
import org.itemscript.core.values.JsonObject;
import org.itemscript.core.values.JsonValue;

public class IdbTable {
	private String tableName;
	private String version;
	private String synchronizingQuery;
	private String key;
	
	private LinkedList<IdbChangeListner> listners;
	private TreeMap<String, TreeMap<String,TreeSet<String>>> indeces;
	private TreeMap<String, JsonObject> data;
	
	public IdbTable(String table, String key) {
		this.tableName = table;
		this.key = key;
		version = "0";
		
		listners = new LinkedList<IdbChangeListner>();
		indeces = new TreeMap<String, TreeMap<String,TreeSet<String>>>();
		data = new TreeMap<String, JsonObject>();
	}
	protected IdbTable(String tableName, JsonObject jsonTable){
		this.tableName = tableName;
		this.key = jsonTable.getString(IndexedDb.KEY);
		version = jsonTable.getString(IndexedDb.VERSION);
		refresh(jsonTable);
	}
	protected void refresh(JsonObject jsonTable){
		version = jsonTable.getString(IndexedDb.VERSION);
		
		listners = new LinkedList<IdbChangeListner>();
		indeces = new TreeMap<String, TreeMap<String,TreeSet<String>>>();
		data = new TreeMap<String, JsonObject>();

		json2Indeces(jsonTable.getObject(IndexedDb.INDECES));
		json2Data(jsonTable.getObject(IndexedDb.DATA));
	}

	public String getVersion(){
		return version;
	}
	public void setVersion(String verion){
		this.version=  verion;
	}	

	public String getSynchronizingQuery(){
		return synchronizingQuery;
	}
	public void setSyncQuery(String sync){
		synchronizingQuery = sync;
	}
	public String getName(){
		return tableName;
	}
	public void setName(String tableName){
		this .tableName = tableName;
	}
	public String getKey(){
		return this.key;
	}
	public void setKey(String key){
		this.key = key;
	}
	public boolean addIndex(String index) {
		if(!data.isEmpty() || hasIndex(index))
			return false;
		indeces.put(index, new TreeMap<String, TreeSet<String>>());
		return true;
	}
	public boolean removeIndex(String index){
		if(!indeces.containsKey(index))
			return false;
		indeces.remove(index);
		return true;
	}
	public boolean hasIndex(String index){
		return indeces.containsKey(index);
	}
	public Set<String> indecesList() {
		return indeces.keySet();
	}
	public void insert(JsonObject row) throws IdbException{		
		insertRow(row);
		
		JsonArray arr = IOGate.SYSTEM.createArray();
		arr.a(row.copy());
		for(IdbChangeListner listner : listners){
			listner.onInsert(arr);
		}
	}
	public void insert(JsonArray rows) throws IdbException{
		for(JsonValue row: rows){
			insertRow(row.asObject());
		}
		for(IdbChangeListner listner : listners){
			listner.onInsert(rows);
		}
	}
	private void insertRow(JsonObject row) throws IdbException{
		if (!row.containsKey(key))
			throw new IdbException("The insearted row has no index");

		String keyValue = row.getString(key);
		if (!row.keySet().containsAll(indeces.keySet()))
			throw new IdbException("one index is missing from the insearted row.");
		if(data.containsKey(keyValue))
			throw new IdbException("Duplicated primary key produced by inserting this row");
		
		data.put(keyValue, row); 
		indexing(keyValue, row);
	}
	/**
	 * The multi-valued index should be a string seperated by comma
	 * @param keyValue
	 * @param row
	 */
	private void indexing(String keyValue, JsonObject row){
		TreeMap<String,TreeSet<String>> tempIndex;
		for(String currentIndex : indeces.keySet()){
			tempIndex = indeces.get(currentIndex);
			String currentValue = row.getString(currentIndex);
			if(currentValue == null || currentValue.isEmpty()) 
				continue;
			for(String tempKey : currentValue.split(",")){
				tempKey = tempKey.trim();
				if (tempIndex.containsKey(tempKey)){
					tempIndex.get(tempKey).add(keyValue);
				}
				else{
					TreeSet<String> ts = new TreeSet<String>();
					ts.add(keyValue);
					tempIndex.put(tempKey, ts);
				}
			}
		}
	}
	public void delete(String  keyValue) throws IdbException{
		JsonObject row = (JsonObject) data.get(keyValue);
		deleteRow(row,  keyValue);
		String []keys = {keyValue};
		for(IdbChangeListner listner : listners){
			listner.onDelete(keys);
		}
	}
	public void delete(String[] keyValues) throws IdbException{
		for (int i = 0 ; i < keyValues.length; i++)
			deleteRow(data.get(keyValues[i]),  keyValues[i]);
		
		for(IdbChangeListner listner : listners){
			listner.onDelete(keyValues);
		}
	}

	public void deleteAll() throws IdbException{
		data.clear();
		for(Iterator <String> i = indeces.keySet().iterator(); i.hasNext();)
			indeces.get(i.next()).clear();
		for(IdbChangeListner listner : listners){
			listner.onDeleteAll();
		}
	}	
	
	private void deleteRow( JsonObject row, String  keyValue){
		if ((data.remove(keyValue)) == null)
			return;

		TreeMap<String,TreeSet<String>> tempIndex;
		TreeSet<String> tempKey;
		for(String currentIndex : indeces.keySet()){
			tempIndex = indeces.get(currentIndex);

			String indexValues = row.getString(currentIndex);
			if(indexValues == null) continue;
			for(String indexValue : indexValues.split(",")){
				indexValue = indexValue.trim();
				
				tempKey = tempIndex.get(indexValue);
				tempKey.remove(keyValue);
				if(tempKey.isEmpty())
					tempIndex.remove(indexValue);
			}
			tempIndex.remove("");
		}

	}
	
	public void update(String keyValue, String columnName, String newValue) throws IdbException{
		JsonObject row = get(keyValue);
		if (row == null)
			throw new IdbException("a row with key value =  [" + keyValue + "] is not exist");
		if(!row.hasString(columnName) && row.getString(columnName) != null)
			throw new IdbException("Column [" + columnName + "] is not exist");

		//update value
		deleteRow(row, keyValue);
		row.removeValue(columnName);
		row.p(columnName, newValue);
		insertRow(row);
		
		for(IdbChangeListner listner : listners){
			listner.onUpdate(row);
		}
	
	}

	public void update(String keyValue, JsonObject newValues) throws IdbException{
		JsonObject row = get(keyValue);
		if (row == null)
			throw new IdbException("a row with key value =  [" + keyValue + "] is not exist");;
		String columnName;
		
		deleteRow(row, keyValue);
		for(Iterator<String> i = newValues.keySet().iterator(); i.hasNext();){
			columnName = i.next();
			if(!row.hasString(columnName))
				throw new IdbException("Column [" + columnName + "] is not exist");
			//update value
			row.removeValue(columnName);
			row.p(columnName, newValues.getString(columnName));
		}
		insertRow(row);
		for(IdbChangeListner listner : listners){
			listner.onUpdate(row);
		}
	}
	public boolean hasRow(String keyValue){		
		return data.containsKey(keyValue);
	}
	public JsonObject get(String keyValue) throws IdbException{
		return  (JsonObject) data.get(keyValue);
	}

	public JsonArray get(String indexName, String indexValue) throws IdbException{
		if(! hasIndex(indexName))
			throw new IdbException("There is no index with this name");

		TreeMap<String, TreeSet<String>> currentIndex = indeces.get(indexName);
		TreeSet<String> keys = currentIndex.get(indexValue);
		JsonArray subSet = IOGate.SYSTEM.createArray();

		if(keys == null) {
			return subSet;
		}
		for(Iterator<String> i = keys.iterator(); i.hasNext();){
			subSet.add( data.get(i.next()).copy());
		}
		return subSet;	
	}
	public JsonArray getAll() throws IdbException{
		JsonArray subSet = IOGate.SYSTEM.createArray();
		for(Iterator<JsonObject> i = data.values().iterator(); i.hasNext();){
			subSet.add(i.next().copy());
		}
		return subSet;
	}	
	
	protected TreeMap<String, JsonObject> getAllData(){
		return new TreeMap<String, JsonObject>(data);
	}
	protected TreeMap<String, JsonObject> getIndexedData(String indexName, String indexValue) throws IdbException{
		if(! hasIndex(indexName))
			throw new IdbException("There is no index with this name");

		TreeMap<String, TreeSet<String>> currentIndex = indeces.get(indexName);
		TreeSet<String> keys = currentIndex.get(indexValue);
		TreeMap<String, JsonObject> subSet = new TreeMap<String, JsonObject>();
		String key;
		if(keys == null) {
			return subSet;
		}
		for(Iterator<String> i = keys.iterator(); i.hasNext();){
			key = i.next();
			subSet.put(key, data.get(key));
		}
		return subSet;	
	}

	public TreeSet<String> getIndexKeys(String indexName, String indexValue) throws IdbException{
		if(! hasIndex(indexName))
			throw new IdbException("There is no index with this name");
		
		TreeSet<String> ts = indeces.get(indexName).get(indexValue);
		return (ts != null ? ts: new TreeSet<String>());
	}
	public void addChangeListner(IdbChangeListner listner){
		listners.add(listner);
	}
	public void removeChangeListner(IdbChangeListner listner){
		listners.remove(listner);
	}
	//checks that table contains any data or not
	public boolean isEmpty(){
		return data.isEmpty();
	}
	public JsonObject getJsonTable(){
		JsonObject json = IOGate.SYSTEM.createObject();
		json.put(IndexedDb.VERSION, version);
		json.put(IndexedDb.KEY, key);
		json.put(IndexedDb.SYNCHRONIZING_QUERY, synchronizingQuery);
		json.put(IndexedDb.INDECES, indeces2Json());
		json.put(IndexedDb.DATA, data2Json(data));
		return json;
	}
	private JsonObject data2Json(TreeMap<String, JsonObject> tm){
		JsonObject json = IOGate.SYSTEM.createObject();
		String key;
		for(Iterator<String> i = tm.keySet().iterator(); i.hasNext();){
			key = i.next();
			json.p(key, tm.get(key).copy().asObject());
		}
		return json;
	}
	private JsonObject indeces2Json(){
		JsonObject jsonIndeces = IOGate.SYSTEM.createObject();
		for (Iterator<String> i = indeces.keySet().iterator(); i.hasNext();){
			String key  = i.next();
			TreeMap <String,TreeSet<String>> currentIndex = indeces.get(key);
			JsonObject jsonIndex = IOGate.SYSTEM.createObject();
			for (Iterator<String> j = currentIndex.keySet().iterator(); j.hasNext();){
				String k = j.next();
				JsonArray json = IOGate.SYSTEM.createArray();
				for(Iterator<String> ii = currentIndex.get(k).iterator(); ii.hasNext();){
					json.add(ii.next());
				}
				jsonIndex.put(k, json);
			}
			jsonIndeces.put(key, jsonIndex);
		}
		return jsonIndeces;
	}
	
	private void json2Indeces(JsonObject jsonIndeces ){
		for(Iterator<String> i = jsonIndeces.keySet().iterator(); i.hasNext();){
			String key = i.next();
			JsonObject jsonindex = jsonIndeces.getObject(key);
			TreeMap<String, TreeSet<String>> index = new TreeMap<String, TreeSet<String>>();
			for(Iterator<String> j = jsonindex.keySet().iterator(); j.hasNext();){
				String k = j.next();
				index.put(k, jsonArray2TreeSet(jsonindex.getArray(k)));
			}
			indeces.put(key, index);
		}	
	}
	private TreeSet<String> jsonArray2TreeSet(JsonArray jsonArray){
		TreeSet<String> ts = new TreeSet<String>();
		int size = jsonArray.size();
		for(int i = 0; i < size; i++){
			ts.add(jsonArray.getString(i));
		}
		return ts;
	}
	private void json2Data(JsonObject jsonObject){
		String key;
		for(Iterator<String> i = jsonObject.keySet().iterator(); i.hasNext();){
			key = i.next();
			data.put(key, jsonObject.get(key).asObject());
		}
	}
}
