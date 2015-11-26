package org.silentsoft.io.memory;

import org.silentsoft.io.data.DataMap;

public final class SharedMemory {

	private static DataMap dataMap = new DataMap();

	public static synchronized DataMap getDataMap() {
		return dataMap;
	}
	
	public static synchronized void setDataMap(DataMap target) {
		dataMap = target;
	}
}
