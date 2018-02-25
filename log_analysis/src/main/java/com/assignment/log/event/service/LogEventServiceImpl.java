package com.assignment.log.event.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.assignment.log.event.model.LogEvent;
import com.assignment.log.event.model.ProcessedLog;

@Service("eventService")
@ComponentScan
@Transactional(propagation = Propagation.REQUIRED)
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
	public void saveLongTimeTakingEvents(ProcessedLog processedLog) throws LogEventServiceException {
		try {
			logger.info("Logging events for debug, Event duration="+processedLog.getEventDuration()+", ID=["+processedLog.getId()+"]");
			
			
			this.getLogEventDao().saveProcessedEventLog(processedLog);
		} catch (DataAccessException e) {
			throw new LogEventServiceException("Error occurred while saving processed event log, root cause - ", e);
		}
	}

	@Override
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
				LogEvent logEntry = reducedLogsMap.get(logEvent.getId());
				if (logEntry != null) {
					long existingTimestamp = logEntry.getTimestamp();
					long newEntryTimestamp = logEvent.getTimestamp();
					long eventDuration = Math.abs(existingTimestamp - newEntryTimestamp);
					if (eventDuration > 6) {
						ProcessedLog timeTakingEvent = new ProcessedLog();
						timeTakingEvent.setHost(logEvent.getHost());
						timeTakingEvent.setEventDuration(eventDuration);
						timeTakingEvent.setId(logEvent.getId());
						timeTakingEvent.setType(logEvent.getType());
						// publish the event
						saveLongTimeTakingEvents(timeTakingEvent);
						logger.debug(" Event Duration=[" + eventDuration + "]");

						logger.info(
								" long time taking event --- Current Thread Name:[" + Thread.currentThread().getName()
										+ "], Log Event Details=[" + timeTakingEvent.toString() + "]");
					}else {
						logger.info(" removing not - long time taking event --- Current Thread Name:["
								+ Thread.currentThread().getName() + "], Event Duration=[" + eventDuration
								+ "], Log Event Details=[" + logEntry + "]");
					}
					logger.info(" Event Duration=[" + eventDuration + "]");
					reducedLogsMap.remove(logEvent.getId());
				} else {
					reducedLogsMap.put(logEvent.getId(), logEvent);
				}
			}

		}

		return this.convertMapValuesToList(reducedLogsMap);
	}
	@Override
	public List<LogEvent> convertMapValuesToList(Map<String, LogEvent> reducedLogsMap) {
		List<LogEvent> eventList = new ArrayList<>();
		if (reducedLogsMap != null) {
			for (Map.Entry<String, LogEvent> entry : reducedLogsMap.entrySet()) {
				eventList.add(entry.getValue());
			}
		}
		return eventList;
	}
}
