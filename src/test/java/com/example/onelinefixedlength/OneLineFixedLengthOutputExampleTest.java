package com.example.onelinefixedlength;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.common.TestBase;
import com.example.onelinefixedlength.config.OneLineFixedLengthOutputExampleConfig;

@SpringBootTest
public class OneLineFixedLengthOutputExampleTest extends TestBase {

	@Autowired
	OneLineFixedLengthOutputExampleConfig config;

	@Test
	void test() throws Exception {
		jobLauncher.run(config.oneLineFixedLengthOutputJob(), new JobParameters());

		assertEquals("00001foo  " +
				"00002bar  " +
				"00003baz  ",
				Files.readString(Path.of("target/files/outputs/onelinefixedlength.txt")));
	}
}
