package com.example.onelinefixedlength.config;

import java.io.BufferedReader;

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
import com.example.common.OneLineToMultiLineBufferedReaderFactory2;
import com.example.onelinefixedlength.item.RecordItem;

/**
 * 複数レコードが1行に収められた固定長ファイルを読み込む例。
 * もうひとつの解。
 * こちらの方がアドホックな対応。
 *
 */
@Configuration
public class OneLineFixedLengthInputExample2Config extends ConfigBase {

	/**
	 * 複数レコードが1行に収められた固定長ファイルを読み込む{@link ItemReader}を構築する。
	 * {@link ItemReader}の実装クラスは{@link FlatFileItemReader}を使えばOK。
	 * そこに{@link BufferedReader#readLine()}をオーバーライドした{@link BufferedReader}を
	 * 返す{@link BufferedReaderFactory}を設定する。
	 * 
	 * @return 複数レコードが1行に収められた固定長ファイルを読み込む{@link ItemReader}
	 */
	@Bean
	@StepScope
	public FlatFileItemReader<RecordItem> oneLineFixedLengthInput2ItemReader() {
		return new FlatFileItemReaderBuilder<RecordItem>()
				.resource(new PathResource("files/inputs/onelinefixedlength.txt"))
				.saveState(false)
				.recordSeparatorPolicy(new IfEmptyThenNullRecordSeparatorPolicy())
				// ★ここがポイント
				.bufferedReaderFactory(new OneLineToMultiLineBufferedReaderFactory2(9))
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
	public PassThroughItemProcessor<RecordItem> oneLineFixedLengthInput2ItemProcessor() {
		return new PassThroughItemProcessor<>();
	}

	/**
	 * テストコードでアサートするため{@link ItemWriter}の実装クラスとして
	 * {@link ListItemWriter}を構築している。
	 * 
	 * @return {@link ListItemWriter}のインスタンス
	 */
	@Bean
	public ListItemWriter<RecordItem> oneLineFixedLengthInput2ItemWriter() {
		return new ListItemWriter<>();
	}

	@Bean
	public Step oneLineFixedLengthInput2Step() {
		return steps.get("OneLineFixedLengthInput2")
				.<RecordItem, RecordItem> chunk(100)
				.reader(oneLineFixedLengthInput2ItemReader())
				.processor(oneLineFixedLengthInput2ItemProcessor())
				.writer(oneLineFixedLengthInput2ItemWriter())
				.build();
	}

	@Bean
	public Job oneLineFixedLengthInput2Job() {
		return jobs.get("OneLineFixedLengthInput2")
				.start(oneLineFixedLengthInput2Step())
				.build();
	}
}
