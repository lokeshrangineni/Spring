package com.assignment.log.event.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.assignment.log.event.config.ApplicationConstants;
import com.assignment.log.event.model.LogEvent;
import com.assignment.log.event.model.ProcessedLog;
/**
 *  Service class implementation for this application.
 * 
 * @author Lokesh
 * Since 02/25/2018
 *
 */
@Service("eventService")
@Transactional(propagation = Propagation.SUPPORTS)
public class LogEventServiceImpl implements ILogEventsService {
	private static final Logger logger = LoggerFactory.getLogger(LogEventServiceImpl.class);
	@Autowired
	private ILogEventsDao logEventDao;

	public ILogEventsDao getLogEventDao() {
		return logEventDao;
	}

	public void setLogEventDao(ILogEventsDao logEventDao) {
		this.logEventDao = logEventDao;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void saveLongTimeTakingEvents(ProcessedLog processedLog) throws LogEventServiceException {
		try {
			this.getLogEventDao().saveProcessedEventLog(processedLog);
		} catch (DataAccessException e) {
			throw new LogEventServiceException("Error occurred while saving processed event log, root cause - ", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void saveOrphanedEventLogs(List<LogEvent> eventsList) throws LogEventServiceException {
		try {
			this.getLogEventDao().saveOrphanedEventLogs(eventsList);
		} catch (DataAccessException e) {
			throw new LogEventServiceException("Error occurred while saving staged event log, root cause - ", e);
		}
	}
	
	@Override
	public List<LogEvent> getOrphanedLogEvents() throws LogEventServiceException {
		List<LogEvent> longTimeTakingEvents;
		try {
			longTimeTakingEvents = this.getLogEventDao().getOrphanedLogEvents();
		} catch (DataAccessException e) {
			throw new LogEventServiceException("Error occurred while retrieving orphaned event logs, root cause - ", e);
		}
		return longTimeTakingEvents;
	}
	

	/**
	 * This method is not implementing pagination so it may not be scalable. 
	 * 
	 */
	@Override
	public List<ProcessedLog> getLongTimeTakingLogEvents() throws LogEventServiceException {
		List<ProcessedLog> longTimeTakingEvents;
		try {
			longTimeTakingEvents = this.getLogEventDao().getLongTimeTakingLogEvents();
		} catch (DataAccessException e) {
			throw new LogEventServiceException("Error occurred while retrieving staged event logs, root cause - ", e);
		}
		return longTimeTakingEvents;
	}

	@Override
	public List<LogEvent> processLogEventsForLongTimeTakingEvents(List<LogEvent> logsList) {
		Map<String, LogEvent> reducedLogsMap = null;
		if (logsList != null) {
			reducedLogsMap = new HashMap<>();
			for (LogEvent logEvent : logsList) {
				//use mapping for indexing. so that it is easy to search
				LogEvent logEntry = reducedLogsMap.get(logEvent.getId());
				if (logEntry != null) {
					long existingTimestamp = logEntry.getTimestamp();
					long newEntryTimestamp = logEvent.getTimestamp();
					long eventDuration = Math.abs(existingTimestamp - newEntryTimestamp);
					if (eventDuration > ApplicationConstants.DEFAULT_CAPTURE_EVENT_TIME) {
						ProcessedLog timeTakingEvent = getProcessedLogEvent(logEvent, eventDuration);
						// publish the event
						saveLongTimeTakingEvents(timeTakingEvent);
						logger.debug(
								" long time taking event --- Current Thread Name:[" + Thread.currentThread().getName()
										+ "], Log Event Details=[" + timeTakingEvent.toString() + "]");
					}else {
						//WE can store all the events in case if we need for further processing. As of now deleting these events.
						logger.debug(" removing not - long time taking event --- Current Thread Name:["
								+ Thread.currentThread().getName() + "], Event Duration=[" + eventDuration
								+ "], Log Event Details=[" + logEntry + "]");
					}
					logger.debug(" Event Duration=[" + eventDuration + "]");
					//we no need to worry about this event any more.
					reducedLogsMap.remove(logEvent.getId());
				} else {
					reducedLogsMap.put(logEvent.getId(), logEvent);
				}
			}

		}

		return this.convertMapValuesToList(reducedLogsMap);
	}

	protected ProcessedLog getProcessedLogEvent(LogEvent logEvent, long eventDuration) {
		ProcessedLog timeTakingEvent = new ProcessedLog();
		timeTakingEvent.setHost(logEvent.getHost());
		timeTakingEvent.setEventDuration(eventDuration);
		timeTakingEvent.setId(logEvent.getId());
		timeTakingEvent.setType(logEvent.getType());
		return timeTakingEvent;
	}
	
	@Override
	public List<LogEvent> convertMapValuesToList(Map<String, LogEvent> reducedLogsMap) {
		List<LogEvent> eventList = null;
		if (reducedLogsMap != null) {
			eventList = new ArrayList<>();
			for (Map.Entry<String, LogEvent> entry : reducedLogsMap.entrySet()) {
				eventList.add(entry.getValue());
			}
		}
		return eventList;
	}
}
