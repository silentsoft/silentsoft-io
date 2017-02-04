package org.silentsoft.io.event;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.silentsoft.io.event.EventHistory.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class EventHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(EventHandler.class);
	
	private static final int MAX_THREAD_POOL = 5;
	
	private static int historyCapacity = 0;
	
	private static List<EventHistory> histories;
	
	private static List<EventListener> listeners;
	
	static {
		/**
		 * Note : ArrayList may occur ConcurrentModificationException
		 * 		  so using CopyOnWriteArrayList for prevent Exception multi thread.
		 * Do not use below source code.
		 * private static List<EventListener> listeners = new ArrayList<EventListener>();
		 */
		
		histories = new CopyOnWriteArrayList<EventHistory>();
		listeners = new CopyOnWriteArrayList<EventListener>();
		
		initHistory(100);
	}
	
	public static synchronized void initHistory(int capacity) {
		getHistories().clear();
		setHistoryCapacity(capacity);
	}
	
	public static synchronized List<EventHistory> getHistories() {
		return histories;
	}
	
	private static synchronized void setHistoryCapacity(int capacity) {
		historyCapacity = capacity;
	}
	
	private static synchronized void addHistory(EventHistory eventHistory) {
		if (getHistoryCapacity() > 0) {
			if (getHistories().size() >= getHistoryCapacity()) {
				getHistories().remove(0);
			}
			
			getHistories().add(eventHistory);
		}
		
		eventHistory = null;
	}
	
	private static synchronized int getHistoryCapacity() {
		return historyCapacity;
	}
	
	public static synchronized void addListener(EventListener eventListener) {
		if (getListeners().indexOf(eventListener) == -1) {
			getListeners().add(eventListener);
		}
	}
	
	private static synchronized List<EventListener> getListeners() {
		return listeners;
	}
	
	public static synchronized void removeListener(EventListener eventListener) {
		if (getListeners().indexOf(eventListener) != -1) {
			getListeners().remove(eventListener);
		}
	}
	
	/**
	 * call event by asynch
	 * @param caller
	 * @param event
	 */
	public static synchronized void callEvent(final Class<?> caller, String event) {
		callEvent(caller, event, true);
	}
	
	/**
	 * if doAsynch is true, event will fire by asynch.
	 * if false, event will fire by synch
	 * @param caller
	 * @param event
	 * @param doAsynch
	 */
	public static synchronized void callEvent(final Class<?> caller, String event, boolean doAsynch) {
		addHistory(new EventHistory(EventType.OCCUR, event, doAsynch, caller, null));
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("[Event occur : <{}> by <{}>]", new Object[]{event, caller.getName()});
		}
		
		if (doAsynch) {
			callEventByAsynch(caller, event);
		} else {
			callEventBySynch(caller, event);
		}
	}
	
	private static synchronized void callEventByAsynch(final Class<?> caller, final String event) {
		ExecutorService executorService = Executors.newFixedThreadPool(MAX_THREAD_POOL);
		
		for (final EventListener listener : listeners) {
			executorService.execute(createEventStub(caller, event, listener, true));
        }
		
		executorService.shutdown();
	}
	
	private static synchronized void callEventBySynch(final Class<?> caller, final String event) {
		for (final EventListener listener : listeners) {
			createEventStub(caller, event, listener, false).run();
        }
	}
	
	private static synchronized Runnable createEventStub(Class<?> caller, String event, EventListener listener, boolean isAsynch) {
		return new Runnable() {
			@Override
			public void run() {
				if (listener.getClass().getName().equals(caller.getName())) {
					addHistory(new EventHistory(EventType.SKIP, event, isAsynch, caller, listener));
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("[Event skip : <{}> by self <{}>]", new Object[]{event, caller.getName()});
					}
	            } else {
	            	addHistory(new EventHistory(EventType.CATCH, event, isAsynch, caller, listener));
	            	if (LOGGER.isDebugEnabled()) {
	            		LOGGER.debug("[Event catch : <{}> by <{}>]", new Object[]{event, listener.getClass().getName()});
	            	}
	                
	                try {
	                	addHistory(new EventHistory(EventType.SCHEDULED, event, isAsynch, caller, listener));
	                	listener.onEvent(event);
	                	addHistory(new EventHistory(EventType.PROCESSED, event, isAsynch, caller, listener));
	                } catch (Exception e) {
	                	addHistory(new EventHistory(EventType.FAILURE, event, isAsynch, caller, listener));
	                	LOGGER.error("[Event failure : <{}> on <{}>]", new Object[]{event, listener.getClass().getName(), e});
	                }
	            }
			}
		};
	}
}
