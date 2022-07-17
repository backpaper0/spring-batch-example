package com.example.onelinefixedlength.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.BufferedReaderFactory;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.batch.item.support.ListItemWriter;
import org.springframework.batch.item.support.PassThroughItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.PathResource;

import com.example.common.ConfigBase;
import com.example.common.IfEmptyThenNullRecordSeparatorPolicy;
import com.example.common.OneLineToMultiLineBufferedReaderFactory;
import com.example.onelinefixedlength.item.RecordItem;

/**
 * 複数レコードが1行に収められた固定長ファイルを読み込む例。
 *
 */
@Configuration
public class OneLineFixedLengthInputExampleConfig extends ConfigBase {

	/**
	 * 複数レコードが1行に収められた固定長ファイルを読み込む{@link ItemReader}を構築する。
	 * {@link ItemReader}の実装クラスは{@link FlatFileItemReader}を使えばOK。
	 * そこに1レコード毎に改行コードを差し込む{@link BufferedReaderFactory}を設定する。
	 * 
	 * @return 複数レコードが1行に収められた固定長ファイルを読み込む{@link ItemReader}
	 */
	@Bean
	@StepScope
	public FlatFileItemReader<RecordItem> oneLineFixedLengthInputItemReader() {
		return new FlatFileItemReaderBuilder<RecordItem>()
				.resource(new PathResource("files/inputs/onelinefixedlength.txt"))
				.saveState(false)
				.recordSeparatorPolicy(new IfEmptyThenNullRecordSeparatorPolicy())
				// ★ここがポイント
				.bufferedReaderFactory(new OneLineToMultiLineBufferedReaderFactory(9))
				.targetType(RecordItem.class)
				.fixedLength()
				.columns(new Range(1, 3), new Range(4, 9))
				.names("id", "name")
				.build();
	}

	/**
	 * 読み込みの例なので{@link ItemProcessor}は何もしない。
	 * 
	 * @return 入力値をそのまま出力する{@link ItemProcessor}
	 */
	@Bean
	public PassThroughItemProcessor<RecordItem> oneLineFixedLengthInputItemProcessor() {
		return new PassThroughItemProcessor<>();
	}

	/**
	 * テストコードでアサートするため{@link ItemWriter}の実装クラスとして
	 * {@link ListItemWriter}を構築している。
	 * 
	 * @return {@link ListItemWriter}のインスタンス
	 */
	@Bean
	public ListItemWriter<RecordItem> oneLineFixedLengthInputItemWriter() {
		return new ListItemWriter<>();
	}

	@Bean
	public Step oneLineFixedLengthInputStep() {
		return steps.get("OneLineFixedLengthInput")
				.<RecordItem, RecordItem> chunk(100)
				.reader(oneLineFixedLengthInputItemReader())
				.processor(oneLineFixedLengthInputItemProcessor())
				.writer(oneLineFixedLengthInputItemWriter())
				.build();
	}

	@Bean
	public Job oneLineFixedLengthInputJob() {
		return jobs.get("OneLineFixedLengthInput")
				.start(oneLineFixedLengthInputStep())
				.build();
	}
}
