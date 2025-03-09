package net.entersol.indexeddb;

import org.itemscript.core.values.JsonArray;
import org.itemscript.core.values.JsonObject;

public interface IdbChangeListner {
	public void onUpdate(JsonObject row);
	public void onDelete(String[] keyValues);
	public void onDeleteAll();
	public void onInsert(JsonArray rows);
}
