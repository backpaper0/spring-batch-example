package com.example.multilayout.config;

import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.FieldExtractor;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.item.support.PassThroughItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.PathResource;

import com.example.common.ConfigBase;
import com.example.multilayout.item.DataRecordItem;
import com.example.multilayout.writer.MultiLayoutFileOutputHeaderFooterCallback;

/**
 * マルチレイアウトの固定長ファイルを書き出す例。
 *
 */
@Configuration
public class MultiLayoutFileOutputExampleConfig extends ConfigBase {

	/**
	 * 決まったデータを返す{@link ItemReader}を構築する。
	 * 
	 * @return {@link ListItemReader}のインスタンス
	 */
	@Bean
	@StepScope
	public ListItemReader<DataRecordItem> multiLayoutFileOutputItemReader() {

		DataRecordItem data1 = new DataRecordItem();
		data1.setClassifier("2");
		data1.setId("00001");
		data1.setName("DATA1");

		DataRecordItem data2 = new DataRecordItem();
		data2.setClassifier("2");
		data2.setId("00002");
		data2.setName("DATA2");

		DataRecordItem data3 = new DataRecordItem();
		data3.setClassifier("2");
		data3.setId("00003");
		data3.setName("DATA3");

		List<DataRecordItem> list = List.of(data1, data2, data3);
		return new ListItemReader<>(list);
	}

	/**
	 * 書き出しの例なので{@link ItemProcessor}は何もしない。
	 * 
	 * @return 入力値をそのまま出力する{@link ItemProcessor}
	 */
	@Bean
	public PassThroughItemProcessor<DataRecordItem> multiLayoutFileOutputItemProcessor() {
		return new PassThroughItemProcessor<>();
	}

	/**
	 * マルチレイアウトのファイルを書き出す{@link FlatFileItemWriter}を構築する。
	 * ヘッダーとフッターはコールバックを設定し、そのコールバックの中で書き出している。
	 * 
	 * @return {@link FlatFileItemWriter}のインスタンス
	 */
	@Bean
	@StepScope
	public FlatFileItemWriter<DataRecordItem> multiLayoutFileOutputItemWriter() {
		return new FlatFileItemWriterBuilder<DataRecordItem>()
				// ★ここがポイント
				.headerCallback(multiLayoutFileOutputHeaderFooterCallback())
				// ★ここがポイント
				.footerCallback(multiLayoutFileOutputHeaderFooterCallback())
				.saveState(false)
				.name("MultiLayoutFileOutput")
				.resource(new PathResource("target/files/outputs/multilayoutoutput.txt"))
				.formatted()
				.format("%s" + "%s" + "%s")
				.fieldExtractor(new FieldExtractorImpl())
				.build();
	}

	/**
	 * ヘッダーおよびフッターのコールバックを構築する。
	 * 
	 * @return ヘッダーおよびフッターのコールバック
	 */
	@Bean
	@StepScope
	public MultiLayoutFileOutputHeaderFooterCallback multiLayoutFileOutputHeaderFooterCallback() {
		return new MultiLayoutFileOutputHeaderFooterCallback();
	}

	@Bean
	public Step multiLayoutFileOutputStep() {
		return steps.get("MultiLayoutFileOutput")
				.<DataRecordItem, DataRecordItem> chunk(100)
				.reader(multiLayoutFileOutputItemReader())
				.processor(multiLayoutFileOutputItemProcessor())
				.writer(multiLayoutFileOutputItemWriter())
				.build();
	}

	@Bean
	public Job multiLayoutFileOutputJob() {
		return jobs.get("MultiLayoutFileOutput")
				.start(multiLayoutFileOutputStep())
				.build();
	}

	static class FieldExtractorImpl implements FieldExtractor<DataRecordItem> {

		@Override
		public Object[] extract(DataRecordItem item) {
			return new Object[] {
					item.getClassifier(),
					item.getId(),
					item.getName()
			};
		}
	}
}
