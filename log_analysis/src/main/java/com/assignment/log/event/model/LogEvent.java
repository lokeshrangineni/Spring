package com.assignment.log.event.model;

import java.util.Objects;

public class LogEvent {
	private String id;
	private String state;
	private String type;
	private String host;
	private long timestamp;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	@Override
    public boolean equals(Object o) {

        if (o == this) return true;
        if (!(o instanceof LogEvent)) {
            return false;
        }
        LogEvent event = (LogEvent) o;
        return  Objects.equals(id, event.id) &&
                Objects.equals(state, event.state);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, state);
    }

	
	@Override
	public String toString() {
		return "In ProcessedLog. ID=["+this.id+"], host=["+this.host+"], state=["+this.state+"], timestamp=["+this.timestamp+"]";
	}
}
