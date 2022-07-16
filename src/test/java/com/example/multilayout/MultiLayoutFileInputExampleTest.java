package com.example.multilayout;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.common.TestBase;
import com.example.multilayout.config.MultiLayoutFileInputExampleConfig;
import com.example.multilayout.item.DataRecordItem;
import com.example.multilayout.item.HeaderRecordItem;
import com.example.multilayout.item.RecordBaseItem;
import com.example.multilayout.item.TrailerRecordItem;

@SpringBootTest
public class MultiLayoutFileInputExampleTest extends TestBase {

	@Autowired
	MultiLayoutFileInputExampleConfig config;

	@Test
	void test() throws Exception {
		jobLauncher.run(config.multiLayoutFileInputJob(), new JobParameters());
		List<? extends RecordBaseItem> items = config.multiLayoutFileInputItemWriter().getWrittenItems();

		assertEquals(7, items.size());
		Iterator<? extends RecordBaseItem> iterator = items.iterator();

		// ヘッダーレコード
		HeaderRecordItem header = (HeaderRecordItem) iterator.next();
		assertEquals("1", header.getClassifier());
		assertEquals(LocalDate.parse("2022-07-17"), header.getDate());
		assertNull(header.getFiller());

		// データレコード
		DataRecordItem data1 = (DataRecordItem) iterator.next();
		assertEquals("2", data1.getClassifier());
		assertEquals("00001", data1.getId());
		assertEquals("DATA1", data1.getName());

		DataRecordItem data2 = (DataRecordItem) iterator.next();
		assertEquals("2", data2.getClassifier());
		assertEquals("00002", data2.getId());
		assertEquals("DATA2", data2.getName());

		DataRecordItem data3 = (DataRecordItem) iterator.next();
		assertEquals("2", data3.getClassifier());
		assertEquals("00003", data3.getId());
		assertEquals("DATA3", data3.getName());

		DataRecordItem data4 = (DataRecordItem) iterator.next();
		assertEquals("2", data4.getClassifier());
		assertEquals("00004", data4.getId());
		assertEquals("DATA4", data4.getName());

		DataRecordItem data5 = (DataRecordItem) iterator.next();
		assertEquals("2", data5.getClassifier());
		assertEquals("00005", data5.getId());
		assertEquals("DATA5", data5.getName());

		// トレーラーレコード
		TrailerRecordItem trailer = (TrailerRecordItem) iterator.next();
		assertEquals("9", trailer.getClassifier());
		assertEquals(5, trailer.getSize());
	}
}
