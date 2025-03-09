package net.entersol.indexeddb;
import java.util.Set;
import java.util.TreeMap;


public class IndexedDb {
	protected static final String VERSION = "version";
	protected static final String KEY = "key";
	protected static final String SYNCHRONIZING_QUERY = "synchronizingQuery";
	protected static final String INDECES = "indeces";
	protected static final String DATA = "data";

	
	private String dbName;
	private TreeMap<String, IdbTable> tables;

	public IndexedDb(String name){
		dbName = name;
		tables = new TreeMap<String, IdbTable>();
	}
	public void addTable(IdbTable table) throws IdbException{
		if (hasTable(table.getName()))
			throw new IdbException("Another table with the same name exists in the database.");
		tables.put(table.getName(), table);
	}

	public void removeTable(String name){
		this.tables.remove(name);
	}
	public boolean hasTable(String name){
		return this.tables.containsKey(name);
	}
	public IdbTable getTable(String name){
		return tables.get(name);
	}
	public Set<IdbTable> getTables(){
		return (Set<IdbTable>) this.tables.values();
	}
	public Set<String> tableList(){
		return tables.keySet();
	}
	public void setName(String name) {
		this.dbName = name;
	}
	public String getName() {
		return dbName;
	}
}