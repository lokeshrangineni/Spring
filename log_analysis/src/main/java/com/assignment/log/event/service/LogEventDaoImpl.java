package com.assignment.log.event.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.assignment.log.event.model.LogEvent;
import com.assignment.log.event.model.LogStateEnum;
import com.assignment.log.event.model.ProcessedLog;
/**
 *  DAO class responsible to interact with database.
 * 
 * @author Lokesh
 * Since 02/25/2018
 *
 */
@Repository
@ComponentScan
public class LogEventDaoImpl implements ILogEventsDao {

	private static final String ORPHANED_EVENTS_SAVE_QUERY = "INSERT INTO LA_ORPHANED_LOG_EVENTS(ID, HOST,STATE,TYPE,TIMESTAMP) VALUES (?, ?,?,?,?)";
	@Autowired
	private JdbcTemplate jdbcTemplate;

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	@Override
	public void saveProcessedEventLog(ProcessedLog processedLog) throws DataAccessException {
		this.getJdbcTemplate().update(
				"INSERT INTO LA_LOG_EVENT_ALERTS(ID, HOST,TYPE,EVENT_DURATION,ALERT) VALUES (?, ?,?,?,?)",
				processedLog.getId(), processedLog.getHost(), processedLog.getType(), processedLog.getEventDuration(),
				true);
	}

	@Override
	public void saveOrphanedEventLogs(List<LogEvent> eventsList) throws DataAccessException {
		final int batchSize = 100;
		for (int j = 0; j < eventsList.size(); j += batchSize) {
			final List<LogEvent> batchList = eventsList.subList(j,
					j + batchSize > eventsList.size() ? eventsList.size() : j + batchSize);
			getJdbcTemplate().batchUpdate(ORPHANED_EVENTS_SAVE_QUERY, new BatchPreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					LogEvent logEvent = batchList.get(i);
					ps.setString(1, logEvent.getId());
					ps.setString(2, logEvent.getHost());
					ps.setString(3, logEvent.getState().toString());
					ps.setString(4, logEvent.getType());
					ps.setLong(5, logEvent.getTimestamp());
				}

				@Override
				public int getBatchSize() {
					return eventsList.size();
				}
			});
		}
	}
	
	@Override
	public List<LogEvent> getOrphanedLogEvents() throws DataAccessException {
		List<LogEvent> results = this.getJdbcTemplate().query(
				"SELECT ID, HOST, TYPE,STATE,TIMESTAMP FROM LA_ORPHANED_LOG_EVENTS", new RowMapper<LogEvent>() {
					@Override
					public LogEvent mapRow(ResultSet rs, int row) throws SQLException {
						LogEvent log = new LogEvent();
						log.setId(rs.getString("ID"));
						log.setHost(rs.getString("HOST"));
						log.setState(LogStateEnum.valueOf(rs.getString("STATE")));
						log.setType(rs.getString("TYPE"));
						log.setTimestamp(rs.getLong("TIMESTAMP"));
						return log;
					}
				});

		return results;
	}

	@Override
	public List<ProcessedLog> getLongTimeTakingLogEvents() throws DataAccessException {
		List<ProcessedLog> results = this.getJdbcTemplate().query(
				"SELECT ID, HOST, TYPE,EVENT_DURATION,ALERT FROM LA_LOG_EVENT_ALERTS", new RowMapper<ProcessedLog>() {
					@Override
					public ProcessedLog mapRow(ResultSet rs, int row) throws SQLException {
						ProcessedLog log = new ProcessedLog();
						log.setId(rs.getString("ID"));
						log.setHost(rs.getString("HOST"));
						log.setType(rs.getString("TYPE"));
						log.setEventDuration(rs.getLong("EVENT_DURATION"));
						log.setAlert(rs.getBoolean("ALERT"));
						return log;
					}
				});

		return results;
	}

}
