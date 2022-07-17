package com.example.onelinefixedlength.config;

import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.item.support.PassThroughItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.PathResource;

import com.example.common.ConfigBase;
import com.example.onelinefixedlength.item.RecordItem;

/**
 * 複数レコードが1行に収められた固定長ファイルを書き出す例。
 *
 */
@Configuration
public class OneLineFixedLengthOutputExampleConfig extends ConfigBase {

	/**
	 * 書き出しの例なので{@link ListItemReader}を使う。
	 * 
	 * @return {@link ListItemReader}インスタンス
	 */
	@Bean
	@StepScope
	public ListItemReader<RecordItem> oneLineFixedLengthOutputItemReader() {
		return new ListItemReader<>(List.of(
				new RecordItem(1, "foo"),
				new RecordItem(2, "bar"),
				new RecordItem(3, "baz")));
	}

	/**
	 * 読み込みの例なので{@link ItemProcessor}は何もしない。
	 * 
	 * @return 入力値をそのまま出力する{@link ItemProcessor}
	 */
	@Bean
	public PassThroughItemProcessor<RecordItem> oneLineFixedLengthOutputItemProcessor() {
		return new PassThroughItemProcessor<>();
	}

	/**
	 * 複数レコードを1行にまとめた固定長ファイルを書き出す{@link ItemWriter}を構築する。
	 * 行のセパレーターに空文字列を設定するだけでOK。
	 * 
	 * @return 複数レコードを1行にまとめた固定長ファイルを書き出す{@link ItemWriter}
	 */
	@Bean
	public FlatFileItemWriter<RecordItem> oneLineFixedLengthOutputItemWriter() {
		return new FlatFileItemWriterBuilder<RecordItem>()
				.resource(new PathResource("target/files/outputs/onelinefixedlength.txt"))
				.formatted()
				.format("%1$05d" + "%2$-5s")
				.names("id", "name")
				// ★ここがポイント
				.lineSeparator("")
				.saveState(false)
				.name("OneLineFixedLengthOutput")
				.build();
	}

	@Bean
	public Step oneLineFixedLengthOutputStep() {
		return steps.get("OneLineFixedLengthOutput")
				.<RecordItem, RecordItem> chunk(100)
				.reader(oneLineFixedLengthOutputItemReader())
				.processor(oneLineFixedLengthOutputItemProcessor())
				.writer(oneLineFixedLengthOutputItemWriter())
				.build();
	}

	@Bean
	public Job oneLineFixedLengthOutputJob() {
		return jobs.get("OneLineFixedLengthOutput")
				.start(oneLineFixedLengthOutputStep())
				.build();
	}
}
