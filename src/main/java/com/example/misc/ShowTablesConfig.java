package com.example.misc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.batch.BatchDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.common.ConfigBase;

@Configuration
public class ShowTablesConfig extends ConfigBase {

	@Autowired
	private DataSource dataSource;
	@Autowired
	@BatchDataSource
	private DataSource batchDataSource;

	@Bean
	public Step showTablesStep() {
		return steps.get("ShowTables")
				.tasklet(new ShowTablesTasklet())
				.build();
	}

	@Bean
	public Job showTablesJob() {
		return jobs.get("ShowTables")
				.start(showTablesStep())
				.build();
	}

	private class ShowTablesTasklet implements Tasklet {

		@Override
		public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
			Logger logger = LoggerFactory.getLogger(ShowTablesTasklet.class);
			for (DataSource ds : List.of(dataSource, batchDataSource)) {
				try (Connection con = ds.getConnection()) {
					DatabaseMetaData dmd = con.getMetaData();
					try (ResultSet rs = dmd.getTables(null, "PUBLIC", null, new String[] { "TABLE" })) {
						List<String> tableNames = new ArrayList<>();
						String catalog = null;
						while (rs.next()) {
							if (catalog == null) {
								catalog = rs.getString("TABLE_CAT");
							}
							tableNames.add(rs.getString("TABLE_NAME"));
						}
						logger.info("Catalog = {}, TableNames = {}", catalog, tableNames);
					}
				}
			}
			return RepeatStatus.FINISHED;
		}
	}
}
