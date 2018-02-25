package com.assignment.log.event.model;

public class ProcessedLog {
	private String id;
	private String type;
	private String host;
	private long eventDuration;
	private boolean alert;

	public boolean isAlert() {
		return alert;
	}

	public void setAlert(boolean alert) {
		this.alert = alert;
	}

	public long getEventDuration() {
		return eventDuration;
	}

	public void setEventDuration(long eventDuration) {
		this.eventDuration = eventDuration;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getHost() {
		return this.host;
	}

	public void setHost(String host) {
		this.host = host;
	}
	
	@Override
	public String toString() {
		return "In ProcessedLog. ID=["+this.id+"], host=["+this.host+"], eventDuration=["+this.eventDuration+"]";
	}
}
