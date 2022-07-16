package com.example.common;

import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class TestBase {

	@Autowired
	protected JobLauncher jobLauncher;
}
