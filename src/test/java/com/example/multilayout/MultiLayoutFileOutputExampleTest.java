package com.example.multilayout;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.common.TestBase;
import com.example.multilayout.config.MultiLayoutFileOutputExampleConfig;

@SpringBootTest
public class MultiLayoutFileOutputExampleTest extends TestBase {

	@Autowired
	MultiLayoutFileOutputExampleConfig config;

	@Test
	void test() throws Exception {
		jobLauncher.run(config.multiLayoutFileOutputJob(), new JobParameters());
		List<String> actual = Files.readAllLines(Path.of("target/files/outputs/multilayoutoutput.txt"));
		List<String> expected = List.of(
				"120220726  ",
				"200001DATA1",
				"200002DATA2",
				"200003DATA3",
				"9         3");
		assertEquals(expected, actual);
	}
}
