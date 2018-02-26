package com.assignment.log.event.service;
/**
 * 
 * Exception class to throw any application exceptions.
 * 
 * @author Lokesh
 * Since 02/25/2018
 *
 */
public class LogEventServiceException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public LogEventServiceException(String message) {
		super(message);
	}
	
	public LogEventServiceException(String message, Throwable cause) {
		super(message, cause);
	}

}
