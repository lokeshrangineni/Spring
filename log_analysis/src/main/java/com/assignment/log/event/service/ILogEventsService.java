package com.assignment.log.event.service;

import java.util.List;
import java.util.Map;

import com.assignment.log.event.model.LogEvent;
import com.assignment.log.event.model.ProcessedLog;
/**
 * service interface to include all the business logic for this application.
 * 
 * @author Lokesh
 * Since 02/25/2018
 *
 */
public interface ILogEventsService {
	
	/**
	 * This service method takes the long time taking events and persists in the database.
	 * @param processedLog
	 * @throws LogEventServiceException
	 */
	public void saveLongTimeTakingEvents(ProcessedLog processedLog) throws LogEventServiceException;
	
	/**
	 * This service method retrieves long time taking events from the database.
	 * 
	 * @return
	 * @throws LogEventServiceException
	 */
	List<ProcessedLog> getLongTimeTakingLogEvents() throws LogEventServiceException;

	
	/***
	 * 
	 * This service method takes any orphaned events to the database.
	 * 
	 * @param eventsList
	 * @throws LogEventServiceException
	 */
	public void saveOrphanedEventLogs(List<LogEvent> eventsList) throws LogEventServiceException;
	
	/**
	 * 
	 * This service method returns the orphaned events from the database.
	 * 
	 * @return
	 * @throws LogEventServiceException
	 */
	List<LogEvent> getOrphanedLogEvents() throws LogEventServiceException;



	/**
	 * This method is expected to take list of events and process for the long time taking events. If the event is not taking long time then ignores that event.
	 * @param logsList
	 * @return
	 */
	List<LogEvent> processLogEventsForLongTimeTakingEvents(List<LogEvent> logsList);

	/**
	 * This event just converts the map values into list.
	 * @param reducedLogsMap
	 * @return
	 */
	List<LogEvent> convertMapValuesToList(Map<String, LogEvent> reducedLogsMap);

	
}
