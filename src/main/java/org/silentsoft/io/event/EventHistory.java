package org.silentsoft.io.event;

import java.util.Date;

public class EventHistory {

	public enum EventType {
		OCCUR,
		SKIP,
		CATCH,
		SCHEDULED,
		PROCESSED,
		FAILURE
	}
	
	private EventType eventType;
	
	private Class<?> caller;
	
	private String event;
	
	private EventListener listener;
	
	private boolean isAsynch;
	
	private Date date;
	
	public EventHistory(EventType eventType, String event, boolean isAsynch, Class<?> caller, EventListener listener) {
		this.date = new Date();
		this.eventType = eventType;
		this.event = event;
		this.isAsynch = isAsynch;
		this.caller = caller;
		this.listener = listener;
	}
	
	public EventType getEventType() {
		return eventType;
	}

	public Class<?> getCaller() {
		return caller;
	}

	public String getEvent() {
		return event;
	}
	
	public EventListener getListener() {
		return listener;
	}

	public boolean isAsynch() {
		return isAsynch;
	}
	
	public Date getDate() {
		return date;
	}

	@Override
	public String toString() {
		return String.format("{Date=%s, EventType=%s, Event=%s, Asynch=%s, Caller=%s, Listener=%s}", getDate(), getEventType(), getEvent(), isAsynch(), getCaller(), getListener());
	}
}
