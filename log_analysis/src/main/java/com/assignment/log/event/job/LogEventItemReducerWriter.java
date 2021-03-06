package com.assignment.log.event.job;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.support.ListItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import com.assignment.log.event.model.LogEvent;
import com.assignment.log.event.service.ILogEventsService;
import com.assignment.log.event.service.LogEventServiceException;
/**
 * Job step writer. This is class is having all the logic to reduce the number of events by finding long time taking events. 
 * This class will span two threads to perform the task in parallel.
 * 
 * @author Lokesh
 * Since 02/25/2018
 *
 */
public class LogEventItemReducerWriter extends ListItemWriter<LogEvent> implements StepExecutionListener {
	private static final Logger log = LoggerFactory.getLogger(LogEventItemReducerWriter.class);

	@Autowired
	ILogEventsService eventService;

	private @Autowired AutowireCapableBeanFactory beanFactory;

	public ILogEventsService getEventService() {
		return eventService;
	}

	public void setEventService(ILogEventsService eventService) {
		this.eventService = eventService;
	}

	ExecutionContext executionContext;

	@Override
	public void write(final List<? extends LogEvent> items) throws Exception {
		List<LogEvent> mergedListLogEvents = mergeChunkListWithPreviousChunkList(items);

		int arraySize = mergedListLogEvents.size();
		if (arraySize < 10) {
			processInSingleThreadWhenChunkIsSmall(mergedListLogEvents);
		}else{
			int arraySplitSize = arraySize / 2;
			processInTwoThreadsWhenChunkIsLarge(mergedListLogEvents,arraySplitSize);
		}
		
	}
	//TODO: change argument type
	private List<LogEvent> mergeChunkListWithPreviousChunkList(final List<? extends LogEvent> items) {
		List<LogEvent> previousChunkProcessedData = (List<LogEvent>) this.executionContext.get("CHUNK_REMAINING_DATA");
		List<LogEvent> currentChunkItems = (List<LogEvent>) items;

		List<LogEvent> mergedListLogEvents = mergeListsWithoutDuplicatesAndNullSafe(previousChunkProcessedData, currentChunkItems);
		this.executionContext.remove("CHUNK_REMAINING_DATA");
		return mergedListLogEvents;
	}
	
	private void processInSingleThreadWhenChunkIsSmall(List<LogEvent> mergedListLogEvents){
		List<LogEvent> reducedResult = this.getEventService().processLogEventsForLongTimeTakingEvents(mergedListLogEvents);
		persistReducedEventLogs(reducedResult);
	}

	
	private void processInTwoThreadsWhenChunkIsLarge(List<LogEvent> mergedListLogEvents, int arraySplitSize){
		log.info("Processing current chunk with two threads. current Chunk size=["+mergedListLogEvents.size()+"]");
		//Ideally we should span number of threads dynamically based on number of cores of the processor
		ExecutorService executor = Executors.newFixedThreadPool(2);
		List<LogEvent> subList1 = (List<LogEvent>) mergedListLogEvents.subList(0, arraySplitSize);
		List<LogEvent> subList2 = (List<LogEvent>) mergedListLogEvents.subList(arraySplitSize, mergedListLogEvents.size());
		
		LongTimeTakingEventsProcessorCallable eventReducerCallable1 = new LongTimeTakingEventsProcessorCallable(subList1);
		beanFactory.autowireBean(eventReducerCallable1);
		LongTimeTakingEventsProcessorCallable eventReducerCallable2 = new LongTimeTakingEventsProcessorCallable(subList2);
		beanFactory.autowireBean(eventReducerCallable2);
		Future<List<LogEvent>> futureTask1 = executor.submit(eventReducerCallable1);
		Future<List<LogEvent>> futureTask2 = executor.submit(eventReducerCallable2);
		
		//we can implement the timeout functionality but that timeout feature can be implemented based on use case.
		while (true) {
			if (futureTask1.isDone() && futureTask2.isDone()) {
			try	{
					handleFutureTaskResult(futureTask1);
					handleFutureTaskResult(futureTask2);
					//shut down executor service
					executor.shutdown();
					log.info(" Successfully done with writer job..Killing the writer thread. Thread Name=["
							+ Thread.currentThread().getName() + "]");
				} catch (InterruptedException | ExecutionException e) {
					log.error(" Exception occurred while performing reducer operation in thread Name=["
							+ Thread.currentThread().getName() + "]");
					throw new LogEventServiceException(e.getMessage(), e);
				}
				log.info(" Successfully done with writer job..Killing the writer thread. Thread Name=["
						+ Thread.currentThread().getName() + "]");
				break;
			}
		}
		
	}
	
	
	public void handleFutureTaskResult(Future<List<LogEvent>> futureTask) throws InterruptedException, ExecutionException {
		if (futureTask.isDone()) {
			List<LogEvent> futureTaskResult;
				futureTaskResult = futureTask.get();
				persistReducedEventLogs(futureTaskResult);
		}
	}

		
	public void persistReducedEventLogs(List<LogEvent> reducedEventLogs) {
		log.debug(
				"Going to persist staged records to Job Execution Context for further processing. Collection=[" + reducedEventLogs + "]");
		if (reducedEventLogs != null & !reducedEventLogs.isEmpty()) {
			List<LogEvent> previousChunkRemainingData = (List<LogEvent>)this.executionContext.get("CHUNK_REMAINING_DATA");
			List<LogEvent> mergedList = reducedEventLogs;
			if(previousChunkRemainingData != null) {
				mergedList =	mergeListsWithoutDuplicatesAndNullSafe(previousChunkRemainingData,reducedEventLogs);
			}
			
			this.executionContext.put("CHUNK_REMAINING_DATA", mergedList);
		}
	}

	public List<LogEvent> mergeListsWithoutDuplicatesAndNullSafe(List<LogEvent> previousChunkRemainingData,
			List<LogEvent> currentChunkItems) {
		if (previousChunkRemainingData == null) {
			previousChunkRemainingData = new ArrayList<LogEvent>();
		}
		
		if (currentChunkItems == null || currentChunkItems.size() == 0) {
			return previousChunkRemainingData;
		}
		
		//remove the duplicate log events in case if there are any. 
		for (LogEvent logEvent : currentChunkItems) {
			if(!previousChunkRemainingData.contains(logEvent)) {
				previousChunkRemainingData.add(logEvent);
			}
		}
		
		return previousChunkRemainingData;
	}

	@Override
	public void beforeStep(StepExecution stepExecution) {
		this.executionContext = stepExecution.getExecutionContext();
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		return null;
	}

}
