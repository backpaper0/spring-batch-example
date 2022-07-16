package com.example.common;

import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.format.support.DefaultFormattingConversionService;

public abstract class ConfigBase {

	@Autowired
	protected StepBuilderFactory steps;
	@Autowired
	protected JobBuilderFactory jobs;
	@Autowired
	protected SqlSessionFactory sqlSessionFactory;

	protected <T> BeanWrapperFieldSetMapper<T> fieldSetMapper(Class<? extends T> type) {
		BeanWrapperFieldSetMapper<T> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
		fieldSetMapper.setTargetType(type);
		// 読み取った項目の前後の空白をトリミングして空文字列ならnullへ変換する
		fieldSetMapper.setCustomEditors(Map.of(String.class, new StringTrimmerEditor(true)));
		// @DateTimeFormatで変換できるようにする
		fieldSetMapper.setConversionService(new DefaultFormattingConversionService());
		return fieldSetMapper;
	}
}
