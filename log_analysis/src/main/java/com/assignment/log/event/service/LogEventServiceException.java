package com.assignment.log.event.service;

public class LogEventServiceException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public LogEventServiceException(String message) {
		super(message);
	}
	
	public LogEventServiceException(String message, Throwable cause) {
		super(message, cause);
	}

}
