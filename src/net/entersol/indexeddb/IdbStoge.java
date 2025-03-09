package net.entersol.indexeddb;
import net.entersol.iogate.IOGate;
import org.itemscript.core.values.JsonObject;
import com.google.gwt.storage.client.Storage;
/**
 * database-metadata : 
 * {
 * 		tableName1: { 
 *     		version: v1
 *     		Key: key1
 *     		SynchronizingQuery : sync_query1
 *  	} 
 *      tableName2: { 
 *     		tableName: name2
 *     		version: v2
 *     		Key: key2
 *     		SynchronizingQuery : sync_query2
 *  	}
 *  }
 * database-tableName: [{},{}]
 * 
 * @author sp
 *
 */
public class IdbStoge{

	private Storage storage;
	private IndexedDb database;
	private JsonObject metadata;
	
	public IdbStoge(IndexedDb database) throws IdbStorageException{
		if(!Storage.isLocalStorageSupported())
			throw new IdbStorageException("HTML5 Storage is not supported!");
		
		storage = Storage.getLocalStorageIfSupported();
		String strMetadata = storage.getItem(database.getName());
		if(strMetadata != null)
			metadata = IOGate.SYSTEM.parse(strMetadata).asObject();
		else
			this.metadata = IOGate.SYSTEM.createObject();
		this.database = database;
	}
	public String getSynchronusQuery(String tableName) throws IdbStorageException{
		JsonObject tableMetadata = metadata.getObject(tableName);
		if(tableMetadata == null)
			throw new IdbStorageException("This table has no saved information [getSynchronusQuery]");
		return tableMetadata.getString(IndexedDb.SYNCHRONIZING_QUERY);
	}
	public String getVersion(String tableName) throws IdbStorageException{
		JsonObject tableMetadata = metadata.getObject(tableName);
		if(tableMetadata == null)
			throw new IdbStorageException("This table has no saved information [getVersion]");
		return tableMetadata.getString(IndexedDb.VERSION);
	}
	public String getKey(String tableName) throws IdbStorageException{
		JsonObject tableMetadata = metadata.getObject(tableName);
		if(tableMetadata == null)
			throw new IdbStorageException("This table has no saved information [getKey]");
		return tableMetadata.getString(IndexedDb.KEY);
	}

	private void updateMetadata(String tableName){
		JsonObject obj = IOGate.SYSTEM.createObject();
		IdbTable table = database.getTable(tableName);
		obj.put(IndexedDb.KEY, table.getKey());
		obj.put(IndexedDb.SYNCHRONIZING_QUERY, table.getSynchronizingQuery());
		obj.put(IndexedDb.VERSION, table.getVersion());
		metadata.put(tableName, obj);
		storage.setItem(database.getName(), metadata.toJsonString());
	}
	private void removeMetadata(String tableName){
		metadata.removeValue(tableName);
		storage.setItem(database.getName(), metadata.toJsonString());
	}
	public void save(String tableName) throws IdbStorageException{
		if(!database.hasTable(tableName))
			throw new IdbStorageException("This table is not exist ");

		String strTable = database.getTable(tableName).getJsonTable().toJsonString();
		storage.setItem(database.getName() + "-" + tableName, strTable);
		updateMetadata(tableName);
	}
	public void load(String tableName) throws IdbStorageException{
		String strTable = storage.getItem(database.getName() + "-" + tableName);

		if (strTable == null && !metadata.containsKey(tableName))
			throw new IdbStorageException("This table is not exist.");
		try {
			JsonObject jsonTable = IOGate.SYSTEM.parse(strTable).asObject();
			if(database.hasTable(tableName)){
				database.getTable(tableName).refresh(jsonTable);
			}
			else{
				IdbTable table = new IdbTable(tableName, jsonTable);
				database.addTable(table);
			}
		} catch (IdbException e) {
			e.printStackTrace();
			throw new IdbStorageException("There was problem while adding table to the database");
		}
	}
	public void remove(String tableName) throws IdbStorageException{
		if(!hasTable(tableName))
			throw new IdbStorageException("There is no table saved with the name [" +tableName+ "]");
		storage.removeItem(database.getName() + "-" + tableName);
		removeMetadata(tableName);
	}
	public boolean hasTable(String tableName){
			return metadata.containsKey(tableName);
	}
	public IndexedDb getDatabase(){
		return database;
	}

}
