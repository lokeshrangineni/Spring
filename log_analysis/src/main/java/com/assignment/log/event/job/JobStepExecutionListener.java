package com.assignment.log.event.job;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.assignment.log.event.model.LogEvent;
import com.assignment.log.event.service.ILogEventsService;

@Component
public class JobStepExecutionListener implements StepExecutionListener {

	private static final Logger log = LoggerFactory.getLogger(JobStepExecutionListener.class);

	@Autowired
	private ILogEventsService eventService;

	public ILogEventsService getEventService() {
		return eventService;
	}

	public void setEventService(ILogEventsService eventService) {
		this.eventService = eventService;
	}

	@Override
	public void beforeStep(StepExecution stepExecution) {

	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		ExecutionContext executionContext = stepExecution.getExecutionContext();
		processRemainingLogEvents(executionContext);
		printOrphanedLogEventsToLogFile();
		return null;
	}

	private void processRemainingLogEvents(ExecutionContext executionContext) {
		List<LogEvent> leftOverLogData = (List<LogEvent>) executionContext.get("CHUNK_REMAINING_DATA");
		List<LogEvent> remainingOrphanedEvents = this.getEventService()
				.processLogEventsForLongTimeTakingEvents(leftOverLogData);
		eventService.saveOrphanedEventLogs(remainingOrphanedEvents);
	}

	private void printOrphanedLogEventsToLogFile() {
		List<LogEvent> orphanedLogEvents = this.getEventService().getOrphanedLogEvents();

		log.info("!!! Total number of Orphaned Events found in this job=[" + orphanedLogEvents.size() + "] ...!!!");

		if (log.isDebugEnabled()) {
			log.debug(
					" ------------------------------------------ Printing Orphaned Log events ------------------------------");
			for (LogEvent orphanedEvent : orphanedLogEvents) {
				log.debug("<" + orphanedEvent + ">");
			}
		}
	}
}
