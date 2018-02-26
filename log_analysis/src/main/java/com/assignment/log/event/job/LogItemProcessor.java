package com.assignment.log.event.job;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import com.assignment.log.event.model.LogEvent;
import com.assignment.log.event.model.LogStateEnum;
/**
 * 
 * This class is responsible to Convert JSON data to Java Pojo bean for further processing. 
 * 
 * 
 * @author Lokesh
 * Since 02/25/2018
 *
 */
public class LogItemProcessor implements ItemProcessor<Map<String,Object>,LogEvent> {

    private static final Logger log = LoggerFactory.getLogger(LogItemProcessor.class);

    @Override
    public LogEvent process(final Map<String,Object> logEventMap) throws Exception {
        final String id = (String)logEventMap.get("id");
        final String state = (String)logEventMap.get("state");
        final String type = (String)logEventMap.get("type");
        final String host = (String)logEventMap.get("host");
        final Long timestamp = (Long)logEventMap.get("timestamp");

        LogEvent event = new LogEvent();
        event.setId(id);
        event.setState(LogStateEnum.valueOf(state));
        event.setType(type);
        event.setHost(host);
        event.setTimestamp(timestamp);
        

        log.info("Converting (" + logEventMap + ") into (" + event + ")");

        return event;
    }

}