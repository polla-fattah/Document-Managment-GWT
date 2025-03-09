package net.entersol.indexeddb;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import net.entersol.iogate.IOGate;
import org.itemscript.core.values.JsonObject;

public class IdbFilter {
	private Set<String> indeces;
	private boolean allFields;
	protected IdbTable table;
	protected TreeMap<String, JsonObject> data;
	protected Iterator<String> iterator;
	private TreeSet<String> fields = new TreeSet<String>();

	public IdbFilter(IdbTable table){
		intialize(table, true);
	}
	public IdbFilter(IdbTable table, boolean allFields){
		intialize(table, allFields);
	}
	public IdbFilter(IdbTable table, String[] fields){
		intialize(table, false);
		for(String field : fields){
			addField(field);
		}
	}
	private void intialize(IdbTable table, boolean allFields){
		this.table = table;
		this.allFields = allFields;
		this.indeces = table.indecesList();

	}
	public void returnAllFields(boolean allFields){
		this.allFields = allFields;
	}
	public boolean addField(String field){
		return fields.add(field);
	}
	public boolean removeField(String field){
		return fields.remove(field);
	}
	public void reset(){
		fields.clear();
		data = null;
		iterator = null;
	}
	public Vector<JsonObject> getAll(){
		Vector<JsonObject> arr = new Vector<JsonObject>();

		if(data == null)
			return arr;
		if(this.allFields){
			return (new Vector<JsonObject>(data.values()));
		}
		else{
			for(JsonObject row: data.values()){
				JsonObject obj = IOGate.SYSTEM.createObject();
				for(String key: fields){
					obj.p(key, row.get(key).stringValue());
				}
				arr.add(obj);
			}
			return arr;
		}
	}
	public JsonObject next(){
		if(iterator == null)
			return null;
		JsonObject row = data.get(iterator.next());
		if(allFields)
			return row;
		else{
			JsonObject obj = IOGate.SYSTEM.createObject();
			for(String key: fields){
				obj.p(key, row.get(key).stringValue());
			}
			return obj;
		}
	}
	public boolean hasNext(){
		if(iterator == null)
			return false;
		return iterator.hasNext();
	}
 
	public void equals(String field, String value){
		boolean indexedField = indeces.contains(field);
		boolean keyField = table.getKey().equals(field);
		if(data == null && keyField){
			data = new TreeMap<String, JsonObject>();
			try {
				data.put(value, table.get(value));
			} catch (IdbException e) {e.printStackTrace();}
		}
		else if(keyField){
			TreeSet<String> ts = new TreeSet<String>();
			ts.add(value);
			data.keySet().retainAll(ts);
		}
		else if(data == null && indexedField){
			try {
				data = table.getIndexedData(field, value);
			} catch (IdbException e) {e.printStackTrace();}
		}
		else if(indexedField){
			try {
				data.keySet().retainAll(table.getIndexKeys(field, value));

			} catch (IdbException e) {e.printStackTrace();}
		}
		else{
			if(data == null)
				data = table.getAllData();
			String currentValue, currentKey;
			
			TreeSet<String> keys = new TreeSet<String>();
			for(Iterator<String> i = data.keySet().iterator(); i.hasNext();){
				currentKey = i.next();
				currentValue = data.get(currentKey).getString(field);
				if(currentValue.equals(value)){
					keys.add(currentKey);
				}
			}
			data.keySet().retainAll(keys);

		}
		iterator = data.keySet().iterator();
	}
	
	public void notEquals(String field, String value){
		if(data == null)
			data = table.getAllData();

		if(table.getKey().equals(field)){
			data.keySet().remove(value);
		}
		else if(indeces.contains(field)){
			try {
				data.keySet().removeAll(table.getIndexKeys(field, value));
			} catch (IdbException e) {e.printStackTrace();}
		}
		else{
			String currentValue, currentKey;
			TreeSet<String> keys = new TreeSet<String>();
			for(Iterator<String> i = data.keySet().iterator(); i.hasNext();){
				currentKey = i.next();
				currentValue = data.get(currentKey).getString(field);
				if(currentValue.equals(value)){
					keys.add(currentKey);
				}
			}
			data.keySet().removeAll(keys);
		}
		iterator = data.keySet().iterator();
	}

	public void in(String field, Vector<String> values){
		findSubset(field, values, false);

	}
	public void notIn(String field, Vector<String> values){
		findSubset(field, values, true);
	}
	private void findSubset(String field, Vector<String> values, boolean not){
		if(data == null)
			data = table.getAllData();
		if(table.getKey().equals(field)){
			if(not)
				data.keySet().removeAll(values);
			else
				data.keySet().retainAll(values);
		}
		else if(indeces.contains(field)){
			Vector<String> allValues = new Vector<String>();
			for(String value : values)
				try {
					allValues.addAll(table.getIndexKeys(field, value));
				} catch (IdbException e) {
					e.printStackTrace();
				}
			if(not)
				data.keySet().removeAll(allValues);
			else
				data.keySet().retainAll(allValues);
		}
		else{
			String currentValue, currentKey;
			TreeSet<String> keys = new TreeSet<String>();
			for(Iterator<String> i = data.keySet().iterator(); i.hasNext();){
				currentKey = i.next();
				currentValue = data.get(currentKey).getString(field);
				for(String value : values)
					if(currentValue.equals(value))
						keys.add(currentKey);
			}
			if(not)
				data.keySet().removeAll(keys);
			else
				data.keySet().retainAll(keys);

		}
		
		iterator = data.keySet().iterator();
	}
}
