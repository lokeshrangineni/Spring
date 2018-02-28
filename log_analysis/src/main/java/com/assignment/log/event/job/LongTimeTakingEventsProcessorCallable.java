package com.assignment.log.event.job;

import java.util.List;
import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.assignment.log.event.model.LogEvent;
import com.assignment.log.event.service.ILogEventsService;
/**
 * This is a callable class so that executor service can span multiple threads to process log events asynchronously. 
 * 
 * @author Lokesh
 * Since 02/25/2018
 *
 */
@Component
@Scope("prototype")
public class LongTimeTakingEventsProcessorCallable implements Callable<List<LogEvent>> {

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

	public LongTimeTakingEventsProcessorCallable(List<LogEvent> subList1) {
		this.logsList = subList1;
	}

	public List<? extends LogEvent> getLogsList() {
		return logsList;
	}

	public void setLogsList(List<LogEvent> logsList) {
		this.logsList = logsList;
	}

	@Override
	public List<LogEvent> call() throws Exception {
		return this.getEventService().processLogEventsForLongTimeTakingEvents(this.logsList);
	}

}
