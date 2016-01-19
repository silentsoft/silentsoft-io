package org.silentsoft.io.event;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.silentsoft.io.event.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class EventHandler {
	
	private static final int MAX_THREAD_POOL = 5;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EventHandler.class);
	
	/**
	 * Note : ArrayList may occur ConcurrentModificationException
	 * 		  so using CopyOnWriteArrayList for prevent Exception multi thread.
	 * Do not use below source code.
	 * private static List<EventListener> listeners = new ArrayList<EventListener>();
	 */
	private static List<EventListener> listeners = new CopyOnWriteArrayList<EventListener>();
	
	private static synchronized List<EventListener> getListeners() {
		return listeners;
	}
	
	public static synchronized void addListener(EventListener eventListener) {
		if (getListeners().indexOf(eventListener) == -1) {
			listeners.add(eventListener);
		}
	}
	
	public static synchronized void removeListener(EventListener eventListener) {
		if (getListeners().indexOf(eventListener) != -1) {
			listeners.remove(eventListener);
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
		if (doAsynch) {
			callEventByAsynch(caller, event);
		} else {
			callEventBySynch(caller, event);
		}
	}
	
	private static synchronized void callEventByAsynch(final Class<?> caller, final String event) {
		ExecutorService executorService = Executors.newFixedThreadPool(MAX_THREAD_POOL);
		
		LOGGER.info("[Event occur : <{}> by <{}>]", new Object[]{event, caller.getName()});
		
		for (final EventListener listener : listeners) {
			executorService.execute(
				new Runnable() {
					public void run() {
						if (listener.getClass().getName().equals(caller.getName())) {
			                LOGGER.info("[Event skip : <{}> by self <{}>]", new Object[]{event, caller.getName()});
			            } else {
			                LOGGER.info("[Event catch : <{}> by <{}>]", new Object[]{event, listener.getClass().getName()});
			                
			                try {
			                	listener.onEvent(event);
			                } catch (Exception e) {
			                	LOGGER.error("[Event error : <{}> on <{}>]", new Object[]{event, listener.getClass().getName(), e});
			                }
			            }
					}
				}
			);
        }
		
		executorService.shutdown();
	}
	
	private static synchronized void callEventBySynch(final Class<?> caller, final String event) {
		LOGGER.info("[Event occur : <{}> by <{}>]", new Object[]{event, caller.getName()});
		
		for (final EventListener listener : listeners) {
			if (listener.getClass().getName().equals(caller.getName())) {
                LOGGER.info("[Event skip : <{}> by self <{}>]", new Object[]{event, caller.getName()});
            } else {
                LOGGER.info("[Event catch : <{}> by <{}>]", new Object[]{event, listener.getClass().getName()});
                
                try {
                	listener.onEvent(event);
                } catch (Exception e) {
                	LOGGER.error("[Event error : <{}> on <{}>]", new Object[]{event, listener.getClass().getName(), e});
                }
            }
        }
	}
}
