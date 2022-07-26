package com.example.multilayout.writer;

import java.io.IOException;
import java.io.Writer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.file.FlatFileFooterCallback;
import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.beans.factory.annotation.Value;

/**
 * ヘッダーおよびフッターのコールバック。
 *
 */
public class MultiLayoutFileOutputHeaderFooterCallback
		implements FlatFileHeaderCallback, FlatFileFooterCallback {

	/**
	 * Spring Batchが用意しているクラス。
	 * ここから読み込み件数やスキップ件数、書き出し件数を取得できる。
	 */
	@Value("#{stepExecution}")
	private StepExecution stepExecution;

	private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("uuuuMMdd");

	/**
	 * ヘッダーを書き出す。
	 * 
	 */
	@Override
	public void writeHeader(Writer writer) throws IOException {
		String format = "%s" + "%s" + "%s";
		Object[] args = {
				"1",
				dateTimeFormatter.format(LocalDate.parse("2022-07-26")), // ※例を複雑にしないため日付を固定している
				"     "
		};
		String header = String.format(format, args);
		writer.write(header);
	}

	/**
	 * フッターを書き出す。
	 * 複数行書き出したい場合は、改行コードを書き出せば良い。
	 * 
	 */
	@Override
	public void writeFooter(Writer writer) throws IOException {
		String format = "%s" + "% 10d" + "% 3d";
		Object[] args = {
				"9",
				stepExecution.getWriteCount(),
				stepExecution.getExecutionContext().getInt("total")
		};
		String footer = String.format(format, args);
		writer.write(footer);
	}
}
