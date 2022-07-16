package com.example.fileanddboutput.config;

import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.mybatis.spring.batch.MyBatisCursorItemReader;
import org.mybatis.spring.batch.builder.MyBatisBatchItemWriterBuilder;
import org.mybatis.spring.batch.builder.MyBatisCursorItemReaderBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.batch.item.support.PassThroughItemProcessor;
import org.springframework.batch.item.support.builder.CompositeItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.PathResource;

import com.example.common.ConfigBase;
import com.example.fileanddboutput.mapper.FileAndDbOutputExampleMapper;
import com.example.fileanddboutput.model.FileAndDbOutputExample;

/**
 * ファイルへの書き出しとDBへの書き出しを同時に行う例。
 * ポイントは複数のItemWriterをCompositeItemWriterで1つにまとめること。
 *
 */
@Configuration
public class FileAndDbOutputExampleConfig extends ConfigBase {

	/**
	 * DBから値を読み込む{@link ItemReader}を構築する。
	 * 
	 * @return DBから値を読み込む{@link ItemReader}
	 */
	@Bean
	@StepScope
	public MyBatisCursorItemReader<FileAndDbOutputExample> fileAndDbOutputItemReader() {
		return new MyBatisCursorItemReaderBuilder<FileAndDbOutputExample>()
				.sqlSessionFactory(sqlSessionFactory)
				.queryId(FileAndDbOutputExampleMapper.class.getName() + ".selectByFlagIsFalse")
				.saveState(false)
				.build();
	}

	/**
	 * 書き出しの例なので{@link ItemProcessor}は何もしない。
	 * 
	 * @return 入力値をそのまま出力する{@link ItemProcessor}
	 */
	@Bean
	public PassThroughItemProcessor<FileAndDbOutputExample> fileAndDbOutputItemProcessor() {
		return new PassThroughItemProcessor<>();
	}

	/**
	 * {@link ItemWriter}を構築する。
	 * 
	 * @return ファイルへの書き出しとDBへの書き出しを行う{@link ItemWriter}
	 */
	@Bean
	@StepScope
	public CompositeItemWriter<FileAndDbOutputExample> fileAndDbOutputItemWriter() {
		// DBへ書き出すItemWriterを構築する
		MyBatisBatchItemWriter<FileAndDbOutputExample> itemWriter1 = new MyBatisBatchItemWriterBuilder<FileAndDbOutputExample>()
				.sqlSessionFactory(sqlSessionFactory)
				.statementId(FileAndDbOutputExampleMapper.class.getName() + ".updateFlagToTrue")
				.build();

		// ファイルへ書き出すItemWriterを構築する
		FlatFileItemWriter<FileAndDbOutputExample> itemWriter2 = new FlatFileItemWriterBuilder<FileAndDbOutputExample>()
				.resource(new PathResource("target/files/outputs/fileanddboutput.txt"))
				.formatted()
				.format("%1$05d" + "%2$-5s")
				.names("id", "name")
				.shouldDeleteIfExists(true)
				.append(false)
				.saveState(false)
				.name("fileAndDbOutput")
				.build();

		// CompositeItemWriterで1つのItemWriterにまとめる
		return new CompositeItemWriterBuilder<FileAndDbOutputExample>()
				.delegates(itemWriter1, itemWriter2)
				.build();
	}

	@Bean
	public Step fileAndDbOutputStep() {
		return steps.get("fileAndDbOutput")
				.<FileAndDbOutputExample, FileAndDbOutputExample> chunk(100)
				.reader(fileAndDbOutputItemReader())
				.processor(fileAndDbOutputItemProcessor())
				.writer(fileAndDbOutputItemWriter())
				.build();
	}

	@Bean
	public Job fileAndDbOutputJob() {
		return jobs.get("fileAndDbOutput")
				.start(fileAndDbOutputStep())
				.build();
	}
}
