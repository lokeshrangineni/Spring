package com.assignment.log.event.job;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import com.assignment.log.event.model.LogEvent;

public class LogItemProcessor implements ItemProcessor<Map<String,Object>,LogEvent> {

    private static final Logger log = LoggerFactory.getLogger(LogItemProcessor.class);

    @Override
    public LogEvent process(final Map<String,Object> person) throws Exception {
        final String id = (String)person.get("id");
        final String state = (String)person.get("state");
        final String type = (String)person.get("type");
        final String host = (String)person.get("host");
        final Long timestamp = (Long)person.get("timestamp");

        LogEvent event = new LogEvent();
        event.setId(id);
        event.setState(state);
        event.setType(type);
        event.setHost(host);
        event.setTimestamp(timestamp);
        

        log.info("Converting (" + person + ") into (" + event + ")");

        return event;
    }

}