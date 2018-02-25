package com.assignment.log.event.service;

import java.util.List;

import org.springframework.dao.DataAccessException;

import com.assignment.log.event.model.LogEvent;
import com.assignment.log.event.model.ProcessedLog;

public interface ILogEventsDao {
	
	public void saveProcessedEventLog(ProcessedLog processedLog) throws DataAccessException;

	public void saveOrphanedEventLogs(List<LogEvent> eventsList) throws DataAccessException;
	
	public List<ProcessedLog> getLongTimeTakingLogEvents() throws DataAccessException;

	public List<LogEvent> getOrphanedLogEvents() throws DataAccessException;
	
}
