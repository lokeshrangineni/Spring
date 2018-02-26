package com.assignment.log.event.model;

import java.util.Objects;
/**
 * POJO class to hold log events coming from the log file. 
 * 
 * @author Lokesh
 * Since 02/25/2018
 *
 */
public class LogEvent {
	private String id;
	private LogStateEnum state;
	private String type;
	private String host;
	private long timestamp;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public LogStateEnum getState() {
		return state;
	}
	public void setState(LogStateEnum state) {
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
		return "In LogEvent: ID=["+this.id+"], host=["+this.host+"], state=["+this.state+"], timestamp=["+this.timestamp+"]";
	}
}
