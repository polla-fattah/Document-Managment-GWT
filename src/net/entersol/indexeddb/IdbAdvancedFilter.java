package net.entersol.indexeddb;

import java.util.Date;
import java.util.Iterator;
import java.util.TreeSet;
import com.google.gwt.i18n.client.DateTimeFormat;


public class IdbAdvancedFilter extends IdbFilter{
	private String dateFormat;
	public IdbAdvancedFilter(IdbTable table){
		super(table);
	}
	public IdbAdvancedFilter(IdbTable table, boolean allFields){
		super(table, allFields);
	}
	public IdbAdvancedFilter(IdbTable table, String[] fields){
		super(table, fields);
	}

	public String getDateFormat() {
		return dateFormat;
	}
	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public void equals(String field, int value){
		if(data == null)
			data = table.getAllData();
		String currentKey;
		int currentValue;
		TreeSet<String> keys = new TreeSet<String>();
		for(Iterator<String> i = data.keySet().iterator(); i.hasNext();){
			currentKey = i.next();
			currentValue = Integer.parseInt(data.get(currentKey).getString(field));
			if(currentValue == value)
				keys.add(currentKey);
		}
		data.keySet().retainAll(keys);

		iterator = data.keySet().iterator();
	}
	public void equals(String field, double value){
		if(data == null)
			data = table.getAllData();

		String currentKey;
		double currentValue;
		TreeSet<String> keys = new TreeSet<String>();
		for(Iterator<String> i = data.keySet().iterator(); i.hasNext();){
			currentKey = i.next();
			currentValue = Double.parseDouble(data.get(currentKey).getString(field));
			if(currentValue == value)
				keys.add(currentKey);
		}
		data.keySet().retainAll(keys);
		iterator = data.keySet().iterator();
	}	
	public void equals(String field, Date value){
		if(data == null)
			data = table.getAllData();

		String currentKey;
		Date currentValue;
		TreeSet<String> keys = new TreeSet<String>();
		DateTimeFormat format = DateTimeFormat.getFormat(dateFormat);
		for(Iterator<String> i = data.keySet().iterator(); i.hasNext();){
			currentKey = i.next();
			currentValue = format.parse(data.get(currentKey).getString(field));

			if(currentValue.compareTo(value) == 0)
				keys.add(currentKey);
		}
		data.keySet().retainAll(keys);
		iterator = data.keySet().iterator();
	}
	
	public void notEquals(String field, int value){
		if(data == null)
			data = table.getAllData();

		String currentKey;
		int currentValue;
		TreeSet<String> keys = new TreeSet<String>();
		for(Iterator<String> i = data.keySet().iterator(); i.hasNext();){
			currentKey = i.next();
			currentValue = Integer.parseInt(data.get(currentKey).getString(field));
			if(currentValue != value){
				keys.add(currentKey);
			}
		}
		data.keySet().retainAll(keys);
		iterator = data.keySet().iterator();
	}
	public void notEquals(String field, double value){
		if(data == null)
			data = table.getAllData();

		String currentKey;
		double currentValue;
		TreeSet<String> keys = new TreeSet<String>();
		for(Iterator<String> i = data.keySet().iterator(); i.hasNext();){
			currentKey = i.next();
			currentValue = Double.parseDouble(data.get(currentKey).getString(field));
			if(currentValue != value){
				keys.add(currentKey);
			}
		}
		data.keySet().retainAll(keys);
		iterator = data.keySet().iterator();
	}	
	public void notEquals(String field, Date value){
		if(data == null)
			data = table.getAllData();
		String currentKey;
		Date currentValue;
		TreeSet<String> keys = new TreeSet<String>();
		DateTimeFormat format = DateTimeFormat.getFormat(dateFormat);
		for(Iterator<String> i = data.keySet().iterator(); i.hasNext();){
			currentKey = i.next();
			currentValue = format.parse(data.get(currentKey).getString(field));

			if(currentValue.compareTo(value) != 0){
				keys.add(currentKey);
			}
		}
		data.keySet().retainAll(keys);
		iterator = data.keySet().iterator();
	}

	
	public void gratorThan(String field, double value){
		if(data == null)
			data = table.getAllData();
		String currentKey;
		double currentValue;
		TreeSet<String> keys = new TreeSet<String>();
		for(Iterator<String> i = data.keySet().iterator(); i.hasNext();){
			currentKey = i.next();
			currentValue = Double.parseDouble(data.get(currentKey).getString(field));
			if(currentValue > value){
				keys.add(currentKey);
			}
		}
		data.keySet().retainAll(keys);
		iterator = data.keySet().iterator();
	}	
	public void gratorThan(String field, Date value){
		if(data == null)
			data = table.getAllData();

		String currentKey;
		Date currentValue;
		TreeSet<String> keys = new TreeSet<String>();
		DateTimeFormat format = DateTimeFormat.getFormat(dateFormat);
		for(Iterator<String> i = data.keySet().iterator(); i.hasNext();){
			currentKey = i.next();
			currentValue = format.parse(data.get(currentKey).getString(field));

			if(currentValue.compareTo(value) == 1){
				keys.add(currentKey);
			}
		}
		data.keySet().retainAll(keys);
		iterator = data.keySet().iterator();
	}

	public void gratorThanOrEqual(String field, int value){
		if(data == null)
			data = table.getAllData();
		String currentKey;
		int currentValue;
		TreeSet<String> keys = new TreeSet<String>();
		for(Iterator<String> i = data.keySet().iterator(); i.hasNext();){
			currentKey = i.next();
			currentValue = Integer.parseInt(data.get(currentKey).getString(field));
			if(currentValue >= value){
				keys.add(currentKey);
			}
		}
		data.keySet().retainAll(keys);
		iterator = data.keySet().iterator();
	}
	public void gratorThanOrEqual(String field, double value){
		if(data == null)
			data = table.getAllData();
		String currentKey;
		double currentValue;
		TreeSet<String> keys = new TreeSet<String>();
		for(Iterator<String> i = data.keySet().iterator(); i.hasNext();){
			currentKey = i.next();
			currentValue = Double.parseDouble(data.get(currentKey).getString(field));
			if(currentValue >= value){
				keys.add(currentKey);
			}
		}
		data.keySet().retainAll(keys);
		iterator = data.keySet().iterator();
	}	

	public void lessThan(String field, int value){
		if(data == null)
			data = table.getAllData();
		String currentKey;
		int currentValue;
		TreeSet<String> keys = new TreeSet<String>();
		for(Iterator<String> i = data.keySet().iterator(); i.hasNext();){
			currentKey = i.next();
			currentValue = Integer.parseInt(data.get(currentKey).getString(field));
			if(currentValue < value){
				keys.add(currentKey);
			}
		}
		data.keySet().retainAll(keys);
		iterator = data.keySet().iterator();
	}
	public void lessThan(String field, double value){
		if(data == null)
			data = table.getAllData();
		String currentKey;
		double currentValue;
		TreeSet<String> keys = new TreeSet<String>();
		for(Iterator<String> i = data.keySet().iterator(); i.hasNext();){
			currentKey = i.next();
			currentValue = Double.parseDouble(data.get(currentKey).getString(field));
			if(currentValue < value){
				keys.add(currentKey);
			}
		}
		data.keySet().retainAll(keys);
		iterator = data.keySet().iterator();
	}	
	public void lessThan(String field, Date value){
		if(data == null)
			data = table.getAllData();
		String currentKey;
		Date currentValue;
		TreeSet<String> keys = new TreeSet<String>();
		DateTimeFormat format = DateTimeFormat.getFormat(dateFormat);
		for(Iterator<String> i = data.keySet().iterator(); i.hasNext();){
			currentKey = i.next();
			currentValue = format.parse(data.get(currentKey).getString(field));

			if(currentValue.compareTo(value) == -1){
				keys.add(currentKey);
			}
		}
		data.keySet().retainAll(keys);
		iterator = data.keySet().iterator();
	}

	public void lessThanOrEqual(String field, int value){
		if(data == null)
			data = table.getAllData();
		String currentKey;
		int currentValue;
		TreeSet<String> keys = new TreeSet<String>();
		for(Iterator<String> i = data.keySet().iterator(); i.hasNext();){
			currentKey = i.next();
			currentValue = Integer.parseInt(data.get(currentKey).getString(field));
			if(currentValue <= value){
				keys.add(currentKey);
			}
		}
		data.keySet().retainAll(keys);
		iterator = data.keySet().iterator();
	}
	public void lessThanOrEqual(String field, double value){
		if(data == null)
			data = table.getAllData();
		String currentKey;
		double currentValue;
		TreeSet<String> keys = new TreeSet<String>();
		for(Iterator<String> i = data.keySet().iterator(); i.hasNext();){
			currentKey = i.next();
			currentValue = Double.parseDouble(data.get(currentKey).getString(field));
			if(currentValue <= value){
				keys.add(currentKey);
			}
		}
		data.keySet().retainAll(keys);
		iterator = data.keySet().iterator();
	}	

}
