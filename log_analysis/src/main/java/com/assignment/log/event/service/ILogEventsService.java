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
	
	public void saveLongTimeTakingEvents(ProcessedLog processedLog) throws LogEventServiceException;

	public void saveOrphanedEventLogs(List<LogEvent> eventsList) throws LogEventServiceException;

	List<ProcessedLog> getLongTimeTakingLogEvents() throws LogEventServiceException;

	List<LogEvent> processLogEventsForLongTimeTakingEvents(List<LogEvent> logsList);

	List<LogEvent> convertMapValuesToList(Map<String, LogEvent> reducedLogsMap);

	List<LogEvent> getOrphanedLogEvents() throws LogEventServiceException;

}
