package com.example.multilayout.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.mapping.PatternMatchingCompositeLineMapper;
import org.springframework.batch.item.file.transform.FixedLengthTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.batch.item.support.ListItemWriter;
import org.springframework.batch.item.support.PassThroughItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.PathResource;

import com.example.common.ConfigBase;
import com.example.common.IfEmptyThenNullRecordSeparatorPolicy;
import com.example.multilayout.item.DataRecordItem;
import com.example.multilayout.item.HeaderRecordItem;
import com.example.multilayout.item.RecordBaseItem;
import com.example.multilayout.item.TrailerRecordItem;

/**
 * マルチレイアウトの固定長ファイルを読み込む例。
 *
 */
@Configuration
public class MultiLayoutFileInputExampleConfig extends ConfigBase {

	/**
	 * マルチレイアウトの固定長ファイルを読み込む{@link ItemReader}を構築する。
	 * {@link ItemReader}の実装クラスは{@link FlatFileItemReader}を使えばOK。
	 * そこにマルチレイアウト用に構築した{@link LineMapper}を設定する。
	 * 
	 * @return マルチレイアウトの固定長ファイルを読み込む{@link ItemReader}
	 */
	@Bean
	@StepScope
	public FlatFileItemReader<RecordBaseItem> multiLayoutFileInputItemReader() {
		return new FlatFileItemReaderBuilder<RecordBaseItem>()
				// ★ここがポイント
				.lineMapper(multiLayoutFileInputLineMapper())
				.resource(new PathResource("files/inputs/multilayout.txt"))
				.saveState(false)
				.recordSeparatorPolicy(new IfEmptyThenNullRecordSeparatorPolicy())
				.build();
	}

	/**
	 * マルチレイアウト用に構築した{@link LineMapper}を構築する。
	 * 
	 * @return マルチレイアウト用に構築した{@link LineMapper}
	 */
	@Bean
	public PatternMatchingCompositeLineMapper<RecordBaseItem> multiLayoutFileInputLineMapper() {

		// PatternMatchingCompositeLineMapperをインスタンス化して、
		// FieldSetMapperとLineTokenizerを設定する。
		// FieldSetMapperとLineTokenizerはいずれもパターンと組にしたMapを構築する。
		PatternMatchingCompositeLineMapper<RecordBaseItem> lineMapper = new PatternMatchingCompositeLineMapper<>();

		// ----------------------------------------------------------------
		// FieldSetMapperのMapを構築する。
		// レコードが1から開始する場合はHeaderRecordクラスにマッピングする。
		// レコードが2から開始する場合はDataRecordクラスにマッピングする。
		// レコードが9から開始する場合はTrailerRecordクラスにマッピングする。
		// FieldSetMapperの構築方法はマッピング先のクラス以外は違いがないため、
		// 親クラスのメソッドに切り出して共通化している。
		Map<String, FieldSetMapper<RecordBaseItem>> fieldSetMappers = new HashMap<>();
		fieldSetMappers.put("1*", fieldSetMapper(HeaderRecordItem.class));
		fieldSetMappers.put("2*", fieldSetMapper(DataRecordItem.class));
		fieldSetMappers.put("9*", fieldSetMapper(TrailerRecordItem.class));
		lineMapper.setFieldSetMappers(fieldSetMappers);

		// ----------------------------------------------------------------
		// LineTokenizerのMapを構築する。
		// FieldSetMapperと同じく、レコードの開始文字が1/2/9それぞれで場合分けしている。
		FixedLengthTokenizer headerLineTokenizer = new FixedLengthTokenizer();
		headerLineTokenizer.setColumns(new Range(1, 1), new Range(2, 9), new Range(10, 11));
		headerLineTokenizer.setNames("classifier", "date", "filler");

		FixedLengthTokenizer dataLineTokenizer = new FixedLengthTokenizer();
		dataLineTokenizer.setColumns(new Range(1, 1), new Range(2, 6), new Range(7, 11));
		dataLineTokenizer.setNames("classifier", "id", "name");

		FixedLengthTokenizer trailerLineTokenizer = new FixedLengthTokenizer();
		trailerLineTokenizer.setColumns(new Range(1, 1), new Range(2, 11));
		trailerLineTokenizer.setNames("classifier", "size");

		Map<String, LineTokenizer> tokenizers = new HashMap<>();
		tokenizers.put("1*", headerLineTokenizer);
		tokenizers.put("2*", dataLineTokenizer);
		tokenizers.put("9*", trailerLineTokenizer);
		lineMapper.setTokenizers(tokenizers);

		return lineMapper;
	}

	/**
	 * 読み込みの例なので{@link ItemProcessor}は何もしない。
	 * 
	 * @return 入力値をそのまま出力する{@link ItemProcessor}
	 */
	@Bean
	public PassThroughItemProcessor<RecordBaseItem> multiLayoutFileInputItemProcessor() {
		return new PassThroughItemProcessor<>();
	}

	/**
	 * テストコードでアサートするため{@link ItemWriter}の実装クラスとして
	 * {@link ListItemWriter}を構築している。
	 * 
	 * @return {@link ListItemWriter}のインスタンス
	 */
	@Bean
	public ListItemWriter<RecordBaseItem> multiLayoutFileInputItemWriter() {
		return new ListItemWriter<>();
	}

	@Bean
	public Step multiLayoutFileInputStep() {
		return steps.get("MultiLayoutFileInput")
				.<RecordBaseItem, RecordBaseItem> chunk(100)
				.reader(multiLayoutFileInputItemReader())
				.processor(multiLayoutFileInputItemProcessor())
				.writer(multiLayoutFileInputItemWriter())
				.build();
	}

	@Bean
	public Job multiLayoutFileInputJob() {
		return jobs.get("MultiLayoutFileInput")
				.start(multiLayoutFileInputStep())
				.build();
	}
}
