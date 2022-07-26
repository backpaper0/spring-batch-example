package com.example.multilayout.processor;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;

import com.example.multilayout.item.DataRecordItem;

public class MultiLayoutFileOutputItemProcessor
		implements ItemProcessor<DataRecordItem, DataRecordItem> {

	/**
	 * Spring Batchが用意しているクラス。
	 * クラス間の値の受け渡しができる。
	 */
	@Value("#{stepExecution}")
	private StepExecution stepExecution;

	@Override
	public DataRecordItem process(DataRecordItem item) throws Exception {

		// totalを取得して、Itemが持つnumberを加算して再設定する。
		// こうすることでnumberの合計値を他のクラス(今回の例ではFlatFileFooterCallback実装クラス)へ渡すことができる。
		int total = stepExecution.getExecutionContext().getInt("total");
		stepExecution.getExecutionContext().putInt("total", total + item.getNumber());

		return item;
	}
}
