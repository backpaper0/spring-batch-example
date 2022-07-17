package com.example.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.springframework.batch.item.file.BufferedReaderFactory;
import org.springframework.core.io.Resource;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OneLineToMultiLineBufferedReaderFactory implements BufferedReaderFactory {

	private final int lineSize;

	@Override
	public BufferedReader create(Resource resource, String encoding) throws UnsupportedEncodingException, IOException {
		InputStream in = resource.getInputStream();
		in = new OneLineToMultiLineInputStreamWrapper(in, lineSize);
		return new BufferedReader(new InputStreamReader(in, encoding));
	}
}
