package com.example.common;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import org.springframework.batch.item.file.BufferedReaderFactory;
import org.springframework.core.io.Resource;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OneLineToMultiLineBufferedReaderFactory2 implements BufferedReaderFactory {

	private final int lineSize;

	@Override
	public BufferedReader create(Resource resource, String encoding) throws UnsupportedEncodingException, IOException {
		InputStream in = new BufferedInputStream(resource.getInputStream());
		byte[] b = new byte[lineSize];
		// ダミーのReaderを渡す
		return new BufferedReader(new StringReader("")) {
			@Override
			public String readLine() throws IOException {
				// InputStreamから1行分のデータを読み取って返す。
				// readLine以外を呼ばれたら終わり、というかなりアドホックな実装。
				// FlatFileItemReaderを見るにreadLineしか使っていないので、これができる。
				int i = in.read(b);
				if (i == -1) {
					return null;
				}
				if (i < lineSize) {
					return null;
				}
				return new String(b, 0, i, encoding);
			}

			@Override
			public void close() throws IOException {
				super.close();
				in.close();
			}
		};
	}
}
