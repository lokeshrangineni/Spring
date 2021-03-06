package com.assignment.log.event.job;

import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.JsonLineMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;

import com.assignment.log.event.config.ApplicationConstants;
import com.assignment.log.event.model.LogEvent;


/**
 * This is class will initiate a batch job to process the log file. This class holds all the configuration related to spring batch job.
 * 
 * @author Lokesh
 * Since 02/25/2018
 *
 */
@Configuration
@EnableBatchProcessing
public class BatchConfiguration {
	private static final Logger log = LoggerFactory.getLogger(BatchConfiguration.class);

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private DataSource dataSource;
    
    @Autowired
    private Environment env;
    
    public JobBuilderFactory getJobBuilderFactory() {
		return jobBuilderFactory;
	}

	public void setJobBuilderFactory(JobBuilderFactory jobBuilderFactory) {
		this.jobBuilderFactory = jobBuilderFactory;
	}

	public StepBuilderFactory getStepBuilderFactory() {
		return stepBuilderFactory;
	}

	public void setStepBuilderFactory(StepBuilderFactory stepBuilderFactory) {
		this.stepBuilderFactory = stepBuilderFactory;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Bean
    public FlatFileItemReader<Map<String, Object>> reader() {
        FlatFileItemReader<Map<String, Object>> reader = new FlatFileItemReader<Map<String, Object>>();
        String fileName = populateFileName();
        reader.setResource(new ClassPathResource(fileName));
        JsonLineMapper jsonLineMapper = new JsonLineMapper();
        reader.setLineMapper(jsonLineMapper);
        return reader;
    }

	private String populateFileName() {
		String fullFilePath = null;
		String finalFileName;
		
		try {
			fullFilePath = env.getProperty("fileName");
		}catch(Exception e) {
			log.info("Environment varialble [fileName] is not found through command line argument, so loading default test file as part of the resources directory.");
		}
		
		if(fullFilePath==null) {
        	finalFileName = "ApplicationLog.json";
        }else {
        	finalFileName = fullFilePath;
        }
		return finalFileName;
	}

    @Bean
    public LogItemProcessor processor() {
        return new LogItemProcessor();
    }

    @Bean
    public LogEventItemReducerWriter logEventReducerwriter() {
    	LogEventItemReducerWriter writer = new LogEventItemReducerWriter();
        return writer;
    }
    
    @Bean
    public Job processLogJob(JobCompletionNotificationListener jobListener, JobStepExecutionListener stepListener) {
        return jobBuilderFactory.get("logAnalyticsJob")
                .incrementer(new RunIdIncrementer())
                .listener(jobListener)
                .flow(logFileProcessingStep(stepListener))
                .end()
                .build();
    }

    @Bean
    public Step logFileProcessingStep(JobStepExecutionListener stepListener) {
    	int chunkSize = ApplicationConstants.DEFAULT_SPRING_BATCH_CHUNK_SIZE;
    	
        return stepBuilderFactory.get("Log_File_Processing_Step")
        		.listener(stepListener)
                .<Map<String, Object>, LogEvent> chunk(chunkSize) //reader batch size
                .reader(reader())
                .processor(processor())
                .writer(logEventReducerwriter())
                .build();
    }
    
   
    
}