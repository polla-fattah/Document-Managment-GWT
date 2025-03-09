package net.entersol.indexeddb;

import java.util.Iterator;
import java.util.TreeMap;

import org.itemscript.core.values.JsonArray;
import org.itemscript.core.values.JsonObject;
import org.itemscript.core.values.JsonValue;

import net.entersol.gdbc.Gdbc;
import net.entersol.gdbc.ResultSet;
import net.entersol.gdbc.SqlException;
import net.entersol.gdbc.SqlStatment;

abstract public class IdbSynchronizer {
	private final static String VERSIONING = "General_Versioninig";
	private final static String TABLE = "table";
	private final static String VERSION = "version";
	private TreeMap<String, Boolean> registered = new TreeMap<String, Boolean>();
	private Gdbc database;
	private IndexedDb indexedDb;
	private IdbStoge storage;
	private int count = 0;
	
	public IdbSynchronizer(Gdbc database, IndexedDb indexedDb){
		this.indexedDb = indexedDb;
		this.database = database;
		try {
			storage = new IdbStoge(indexedDb);
		} catch (IdbStorageException e1) {
			onSynchronizeFailed(new IdbSynchronizerException("Can not connect to the local storage"));
		}
	}
	public IdbSynchronizer(Gdbc database, IndexedDb indexedDb, IdbStoge storage){
		this.indexedDb = indexedDb;
		this.database = database;
		this.storage = storage;
	}

	/**
	 * @param database the indexedDb to set
	 */
	public void setDatabase(Gdbc database) {
		this.database = database;
	}
	/**
	 * @return the indexedDb
	 */
	public Gdbc getDatabase() {
		return database;
	}
	/**
	 * @param indexedDb the indexedDb to set
	 */
	public void setIndexedDb(IndexedDb indexedDb) {
		this.indexedDb = indexedDb;
	}
	/**
	 * @return the indexedDb
	 */
	public IndexedDb getIndexedDb() {
		return indexedDb;
	}

	abstract public void onSynchronizeSuccess();
	abstract public void onSynchronizeFailed(IdbSynchronizerException exception);

	public void register(String tableName, boolean useLocalStorage) throws IdbSynchronizerException{
		if(!storage.hasTable(tableName) && !indexedDb.hasTable(tableName))
			throw new IdbSynchronizerException("There is no avalable information for table [ " + tableName + " ]");
		if(!useLocalStorage && !indexedDb.hasTable(tableName))
			throw new IdbSynchronizerException("There is no avalable information for table [ " + tableName + " ] in the provided IndexedDb.");
		registered.put(tableName, useLocalStorage);
	}
	
	public void register(String tableName,String key, String sync,String version, String indeces[], boolean useLocalStorage) throws IdbSynchronizerException{
		if(indexedDb.hasTable(tableName))
			throw new IdbSynchronizerException("Another table with the same name exists in the provided database.");
		
		IdbTable table = new IdbTable(tableName, key);
		table.setSyncQuery(sync);
		table.setVersion(version);
		for(String index: indeces){
			table.addIndex(index);
		}
		try {
			indexedDb.addTable(table);
		} catch (IdbException e) {
			throw new IdbSynchronizerException(e.getMessage());
		}
		registered.put(tableName, useLocalStorage);
	}
	
	public void remove(String tableName){
		registered.remove(tableName);
	}
	public void clear(){
		registered.clear();
		count = 0;
	}

	public void synchronize(){
		String tables  = "";
		for(Iterator<String> i = registered.keySet().iterator(); i.hasNext();){
			tables = tables + i.next();
			if(i.hasNext())
				tables = tables + SqlStatment.COMMA;
		}
		SqlStatment sql = new SqlStatment(SqlStatment.INDIRECT, VERSIONING + SqlStatment.COLON + tables) {
			@Override
			public void onQuerySuccess(ResultSet result){
				JsonArray arrayResult = result.getResults();

				TreeMap<String, Boolean> temp = new TreeMap<String, Boolean>(registered);
				//remove tables that has sync information
				for(JsonValue s : arrayResult){
					JsonObject o = s.asObject();
					temp.remove(o.getString(TABLE));
				}
				// remove table that do not need sync information
				for(String ss : temp.keySet()){
					if(!temp.get(ss))
						temp.remove(ss);
				}
				if(!temp.isEmpty()){
					onSynchronizeFailed(new IdbSynchronizerException("There is no synchronization informaton on the server for this/these table(s) " + temp.keySet().toString()));
				}
				else{
					synchronize(arrayResult);
				}
			}
			
			@Override
			public void onQueryFailure(SqlException sqlex) {
				onSynchronizeFailed(new IdbSynchronizerException("Problem occured while synchronizing" + sqlex.getMessage()));
			}
		};
		database.register(sql);
		database.submit();
	}
	private void synchronize(JsonArray arr) {
		String version, tableName, currentVersion, key, sync;
		
		for(JsonValue ss : arr){
			JsonObject o = ss.asObject();
			version = o.getString(VERSION);
			tableName = o.getString(TABLE);
			
			
			boolean tableIsStored = storage.hasTable(tableName);
			boolean tableIsInMemory = indexedDb.hasTable(tableName);
			boolean useStorage = registered.get(tableName);
			try {
				if(useStorage){
					if(tableIsStored){
						currentVersion = storage.getVersion(tableName);
						key = storage.getKey(tableName);
						sync = storage.getSynchronusQuery(tableName);
					}
					else{
						IdbTable table = indexedDb.getTable(tableName);
						currentVersion = table.getVersion() ;
						key = table.getKey();
						sync = table.getSynchronizingQuery() ;					
					}
					
					if(!version.equals(currentVersion)){
						database.register(new SynchronizeStatment(tableName, key, version, sync, useStorage));
						count++;
					}
					else{
						if(!tableIsInMemory || !currentVersion.equals(indexedDb.getTable(tableName).getVersion())){
							storage.load(tableName);
						}
					}
				}
				else if(tableIsInMemory){
					IdbTable table = indexedDb.getTable(tableName);
					currentVersion = table.getVersion();
					key = table.getKey();
					sync = table.getSynchronizingQuery() ;
					if(!version.equals(currentVersion)){
						database.register(new SynchronizeStatment(tableName, key, version, sync, useStorage));
						count++;
					}
				}
				else{
					onSynchronizeFailed(new IdbSynchronizerException("No Information on the avalable for this table"));

				}

			}
			catch (IdbStorageException e) {
				clear();
				onSynchronizeFailed(new IdbSynchronizerException("problem occure while preparing to the synchronization | " + e.getMessage()));
			}
			
		}
		if(count == 0){
			onSynchronizeSuccess();
		}
		else{
			database.submit();
			//database.clear(); // can not change to our clear() method because here count should preserve his data;
		}
	}

	
	private class SynchronizeStatment extends SqlStatment{
		private String tableName, newVersion, key, syncQuery;
		private boolean useStorage;
		public SynchronizeStatment(String tableName, String key, String newVersion, String syncQuery, boolean useStorage) throws IdbStorageException {
			super(SqlStatment.INDIRECT, syncQuery);
			this.tableName = tableName;
			this.newVersion = newVersion;
			this.key = key;
			this.syncQuery = syncQuery;
			this.useStorage = useStorage;
		}
		@Override
		public void onQuerySuccess(ResultSet result) {
			try {
				IdbTable idt;
				if(indexedDb.hasTable(tableName)){
					idt = indexedDb.getTable(tableName);
					idt.deleteAll();
				}
				else{
					idt = new IdbTable(tableName, key);
					idt.setSyncQuery(syncQuery);
					indexedDb.addTable(idt);
				}
				idt.insert(result.getResults());
				idt.setVersion(this.newVersion);
				if(useStorage)
					storage.save(this.tableName);
				count--;
				if(count == 0)
					onSynchronizeSuccess();
			} catch (IdbException e1) {
				clear();
				onSynchronizeFailed(new IdbSynchronizerException(e1.getMessage()));
			} catch (IdbStorageException e2) {
				clear();
				onSynchronizeFailed(new IdbSynchronizerException(e2.getMessage()));
			}
		}

		@Override
		public void onQueryFailure(SqlException sqlex) {
			clear();
			onSynchronizeFailed(new IdbSynchronizerException(sqlex.getMessage()));
		}
		
	}
}
