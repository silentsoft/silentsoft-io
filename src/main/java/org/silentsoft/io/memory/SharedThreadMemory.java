package org.silentsoft.io.memory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.silentsoft.io.data.DataMap;

public class SharedThreadMemory {
	
	private static Map<Long, DataMap> cache = new ConcurrentHashMap<Long, DataMap>();
	
	public static void create() {
		synchronized (cache) {
			cache.put(Thread.currentThread().getId(), new DataMap());
		}
	}
	
	public static void delete() {
		synchronized (cache) {
			cache.remove(Thread.currentThread().getId());
		}
	}
	
	
	private static DataMap getDataMap() {
		synchronized (cache) {
			return cache.get(Thread.currentThread().getId());
		}
	}
	
	public static Object get(String key) {
		return getDataMap().get(key);
	}
	
	public static void put(String key, Object value) {
		getDataMap().put(key, value);
	}
	
	public static boolean contains(String key) {
		return getDataMap().containsKey(key);
	}
	
	public static Object remove(String key) {
		return getDataMap().remove(key);
	}
	
}
