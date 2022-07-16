package com.example.fileanddboutput;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.common.TestBase;
import com.example.fileanddboutput.config.FileAndDbOutputExampleConfig;
import com.example.fileanddboutput.mapper.FileAndDbOutputExampleMapper;

@SpringBootTest
public class FileAndDbOutputExampleTest extends TestBase {

	@Autowired
	FileAndDbOutputExampleConfig config;
	@Autowired
	FileAndDbOutputExampleMapper mapper;

	@Test
	void test() throws Exception {
		jobLauncher.run(config.fileAndDbOutputJob(), new JobParameters());

		assertEquals(0, mapper.selectByFlagIsFalse().size());

		assertEquals(
				List.of(
						"00002bar  ",
						"00003baz  ",
						"00004qux  "),
				Files.readAllLines(Path.of("target/files/outputs/fileanddboutput.txt")));
	}
}
