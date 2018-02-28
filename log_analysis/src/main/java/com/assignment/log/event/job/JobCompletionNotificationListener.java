package com.assignment.log.event.job;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.assignment.log.event.config.ApplicationConstants;
import com.assignment.log.event.model.ProcessedLog;
import com.assignment.log.event.service.ILogEventsService;

/**
 * Job processing listener to capture starting and ending events so that we can implement some logic.
 * 
 * @author Lokesh
 * Since 02/25/2018
 */
@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

	private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);
	@Autowired
	private ILogEventsService eventService;

	public ILogEventsService getEventService() {
		return eventService;
	}

	public void setEventService(ILogEventsService eventService) {
		this.eventService = eventService;
	}
	
	@Override
	public void afterJob(JobExecution jobExecution) {
		if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
			printLongTimeTakingEventsToLog();
			
			log.info("!!! JOB FINISHED SUCCESSFULLY...!!!");
			System.exit(0);
		}
	}

	
		
	private void printLongTimeTakingEventsToLog() {
		List<ProcessedLog> longTimeTakingLogEvents = eventService.getLongTimeTakingLogEvents();
		log.info("!!! Total number of events=["+longTimeTakingLogEvents.size()+"] taking more than ["+ApplicationConstants.DEFAULT_CAPTURE_EVENT_TIME+"] seconds...!!!");
		
		log.info(" ------------------------------------------ Printing long time taking events ------------------------------");
		for (ProcessedLog longTimeTakingLogEvent : longTimeTakingLogEvents) {
			log.info("<"+ longTimeTakingLogEvent + ">");
		}
	}


	
	
	
}