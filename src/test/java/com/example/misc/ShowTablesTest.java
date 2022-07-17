package com.example.misc;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.common.TestBase;

@SpringBootTest
public class ShowTablesTest extends TestBase {

	@Autowired
	ShowTablesConfig config;

	@Test
	void test() throws Exception {
		jobLauncher.run(config.showTablesJob(), new JobParameters());
	}
}
