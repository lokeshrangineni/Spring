package com.assignment.log.event.job;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.assignment.log.event.model.LogEvent;
import com.assignment.log.event.model.ProcessedLog;
import com.assignment.log.event.service.ILogEventsService;
import com.assignment.log.event.service.LogEventServiceException;

@ComponentScan
@Component
@Scope("prototype")
public class LogEventsReducerCallable implements Callable<Map<String, LogEvent>> {

	private static final Logger logger = LoggerFactory.getLogger(LogEventsReducerCallable.class);
	private List<LogEvent> logsList;

	@Autowired
	@Qualifier("eventService")
	private ILogEventsService eventService;

	public ILogEventsService getEventService() {
		return eventService;
	}

	public void setEventService(ILogEventsService eventService) {
		this.eventService = eventService;
	}

	public LogEventsReducerCallable(List<LogEvent> subList1) {
		this.logsList = subList1;
	}

	public List<? extends LogEvent> getLogsList() {
		return logsList;
	}

	public void setLogsList(List<LogEvent> logsList) {
		this.logsList = logsList;
	}

	@Override
	public Map<String, LogEvent> call() throws Exception {
		Map<String, LogEvent> reducedLogsMap = new HashMap<>();
		if (logsList != null) {
			for (LogEvent logEvent : logsList) {
				LogEvent logEntry = reducedLogsMap.get(logEvent.getId());
				if (logEntry != null) {
					long existingTimestamp = logEntry.getTimestamp();
					long newEntryTimestamp = logEvent.getTimestamp();
					long eventDuration = Math.abs(existingTimestamp - newEntryTimestamp);
					if (eventDuration > 4) {
						ProcessedLog timeTakingEvent = new ProcessedLog();
						timeTakingEvent.setHost(logEvent.getHost());
						timeTakingEvent.setEventDuration(eventDuration);
						timeTakingEvent.setId(logEvent.getId());
						timeTakingEvent.setType(logEvent.getType());
						// publish the event
						persistTimeTakingEvent(timeTakingEvent);
						logger.info(" Event Duration=[" + eventDuration + "]");
						
						logger.info(
								" long time taking event --- Current Thread Name:[" + Thread.currentThread().getName()
										+ "], Log Event Details=[" + timeTakingEvent.toString() + "]");
					}
					logger.info(" Event Duration=[" + eventDuration + "]");
					
					logger.info(" removing not - long time taking event --- Current Thread Name:["
							+ Thread.currentThread().getName() + "], Event Duration=[" + eventDuration
							+ "], Log Event Details=[" + logEntry + "]");
					reducedLogsMap.remove(logEvent.getId());
				} else {
					reducedLogsMap.put(logEvent.getId(), logEvent);
				}
			}

		}
		return reducedLogsMap;
	}

	private void persistTimeTakingEvent(ProcessedLog timeTakingEvent) {
		try {
			this.getEventService().saveProcessedEventLog(timeTakingEvent);
		} catch (LogEventServiceException e) {
			logger.error("Exception occurred while saving time taking events. Root cause-", e);
			throw e;
		}

	}
}
