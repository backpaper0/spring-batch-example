package com.example.multilayout.config;

import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.FieldExtractor;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.PathResource;

import com.example.common.ConfigBase;
import com.example.multilayout.item.DataRecordItem;
import com.example.multilayout.processor.MultiLayoutFileOutputItemProcessor;
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
		data1.setNumber(10);

		DataRecordItem data2 = new DataRecordItem();
		data2.setClassifier("2");
		data2.setId("00002");
		data2.setName("DATA2");
		data2.setNumber(20);

		DataRecordItem data3 = new DataRecordItem();
		data3.setClassifier("2");
		data3.setId("00003");
		data3.setName("DATA3");
		data3.setNumber(30);

		List<DataRecordItem> list = List.of(data1, data2, data3);
		return new ListItemReader<>(list);
	}

	/**
	 * {@link MultiLayoutFileOutputItemProcessor}のインスタンスを構築する。
	 * 
	 * @param stepExecution Spring Batchが用意しているクラス。クラス間の値の受け渡しができる
	 * @return {@link MultiLayoutFileOutputItemProcessor}のインスタンス
	 */
	@Bean
	@StepScope
	public MultiLayoutFileOutputItemProcessor multiLayoutFileOutputItemProcessor(
			@Value("#{stepExecution}") StepExecution stepExecution) {
		// totalの初期値を設定する
		stepExecution.getExecutionContext().putInt("total", 0);
		return new MultiLayoutFileOutputItemProcessor();
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
				.format("%s" + "%-5s" + "%-5s" + "% 3d")
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
				.processor(multiLayoutFileOutputItemProcessor(null))
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
					item.getName(),
					item.getNumber()
			};
		}
	}
}
