package com.example.common;

import org.springframework.batch.item.file.separator.SimpleRecordSeparatorPolicy;

public class IfEmptyThenNullRecordSeparatorPolicy extends SimpleRecordSeparatorPolicy {

	@Override
	public String postProcess(String record) {
		String line = super.postProcess(record);
		if (line != null && line.isEmpty()) {
			return null;
		}
		return line;
	}
}
