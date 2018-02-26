package com.assignment.log.event.service;

import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.assignment.log.event.model.LogEvent;
import com.assignment.log.event.model.LogStateEnum;
import com.assignment.log.event.model.ProcessedLog;




/**
 * This class will be having unit tests for {@link LogEventServiceImpl} 
 * 
 * @author Lokesh
 * Since 02/25/2018
 *
 */
public class LogEventServiceImplTest {
	
	LogEventServiceImpl logEventServiceImpl = new LogEventServiceImpl();
	

	@Test
	public void getProcessedLogEvent_When_LogEventBean_Is_Passed_Then_Verify_ProcessedLogEvent_Is_Populated() {
		//create test data 
		LogEvent logEvent = new LogEvent();
		logEvent.setHost("Test_Host");
		logEvent.setId("Test_ID");
		logEvent.setState(LogStateEnum.STARTED);
		logEvent.setType("APPLICATION");
		
		ProcessedLog processedLogEvent = logEventServiceImpl.getProcessedLogEvent(logEvent, 6);
		
		Assert.assertEquals(Long.valueOf(6), Long.valueOf(processedLogEvent.getEventDuration()));
		Assert.assertEquals("Test_Host", processedLogEvent.getHost());
		Assert.assertEquals("Test_ID", processedLogEvent.getId());
		Assert.assertEquals("APPLICATION", processedLogEvent.getType());
	}
	
	@Test
	public void processLogEventsForLongTimeTakingEvents_When_LogList_Is_Null_Then_Verify_Returns_Null() {
		LogEventServiceImpl spiedEventService = Mockito.spy(LogEventServiceImpl.class);
		when(spiedEventService.convertMapValuesToList(null)).thenReturn(null);
		List<LogEvent> remainingEvents = spiedEventService.processLogEventsForLongTimeTakingEvents(null);
		Assert.assertNull(remainingEvents);
	}

}
