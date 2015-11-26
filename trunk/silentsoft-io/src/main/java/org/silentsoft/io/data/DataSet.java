package org.silentsoft.io.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class DataSet {
	
	/**
	 * column.
	 * param1 : String  : name of set
	 * param2 : Integer : index of set
	 * param3 : Object  : data of set
	 */
	private Map<String, Map<Integer, Object>> column;
	
	/**
	 * dataset
	 * param1 : Integer : row of set
	 * param2 : Map     : column
	 */
	private Map<Integer, Map<String, Map<Integer, Object>>> dataset;
	
	public DataSet(String[] columnName) {
		column = new TreeMap<String, Map<Integer, Object>>();
		dataset = new TreeMap<Integer, Map<String, Map<Integer, Object>>>();
		
		int nColCnt = columnName.length;
		for (int i=0; i<nColCnt; i++) {
			column.put(columnName[i], new HashMap<Integer, Object>());
			column.get(columnName[i]).put(i, new Object());
		}
	}
	
	public int getColumn(String sName) {
		return column.get(sName).keySet().iterator().next();
	}
	
	public String getColumnName(int nCol) {
		String colName = "";
		Iterator<String> colNameItr = column.keySet().iterator();
		
		while((colName=colNameItr.next()) != null) {
			if (column.get(colName).keySet().iterator().next() == nCol) {
				break;
			}
		}
		
		return colName;
	}
	
	public int getColumnCount() {
		return column.size();
	}
	
	public int getRowCount() {
		return dataset.size();
	}
	
	public Object getData(int nRow, int nCol) {
		return dataset.get(nRow).get(getColumnName(nCol)).values().iterator().next();
	}
	
	public void setData(int nRow, int nCol, Object oValue) {
		dataset.get(nRow).get(getColumnName(nCol)).put(nCol, oValue);
	}
	
	public Object getDataByName(String sName, int nRow) {
		return dataset.get(nRow).get(sName).values().iterator().next();
	}
	
	public void setDataByName(String sName, int nRow, Object oValue) {
		dataset.get(nRow).get(sName).put(getColumn(sName), oValue);
	}
	
	public void init() {
		int nLength = getRowCount();
		for (int i=0; i<nLength; i++) {
			removeRow(i);
		}
	}
	
	public void insertRow(int nRow) {
		int nRowCnt = getRowCount();
		int nColCnt = getColumnCount();
		Map<String, Map<Integer, Object>> column = new TreeMap<String, Map<Integer, Object>>();
		
		for (int i=0; i<nColCnt; i++) {
			column.put(getColumnName(i), new HashMap<Integer, Object>());
			column.get(getColumnName(i)).put(this.column.get(getColumnName(i)).keySet().iterator().next(), new Object());
		}
		
		for (int i=0; i<nRow; i++) {
			if (dataset.get(i) == null) {
				dataset.put(i, column);
			}
		}
		
		if (dataset.get(nRow) != null) {
			for (int i=nRowCnt-1; i>=nRow; i--) {
				dataset.put(i+1, dataset.get(i));
			}
		}
		
		dataset.put(nRow, column);
	}
	
	public void removeRow(int nRowIdx) {
		int nRowCnt = getRowCount();
		for (int i=nRowIdx+1; i<nRowCnt; i++) {
			dataset.put(i-1, dataset.get(i));
		}
		
		dataset.remove(nRowCnt-1);
	}
}
