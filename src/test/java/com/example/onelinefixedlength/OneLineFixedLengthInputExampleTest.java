package com.example.onelinefixedlength;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.common.TestBase;
import com.example.onelinefixedlength.config.OneLineFixedLengthInputExampleConfig;
import com.example.onelinefixedlength.item.RecordItem;

@SpringBootTest
public class OneLineFixedLengthInputExampleTest extends TestBase {

	@Autowired
	OneLineFixedLengthInputExampleConfig config;

	@Test
	void test() throws Exception {
		jobLauncher.run(config.oneLineFixedLengthInputJob(), new JobParameters());
		List<? extends RecordItem> items = config.oneLineFixedLengthInputItemWriter().getWrittenItems();

		RecordItem item1 = new RecordItem();
		item1.setId(1);
		item1.setName("foo");

		RecordItem item2 = new RecordItem();
		item2.setId(2);
		item2.setName("bar");

		RecordItem item3 = new RecordItem();
		item3.setId(3);
		item3.setName("baz");

		assertEquals(List.of(item1, item2, item3), items);
	}
}
