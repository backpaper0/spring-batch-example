package com.example.common;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class OneLineToMultiLineInputStreamWrapper extends FilterInputStream {

	private static final int LF = '\n';
	private final int lineSize;
	private int readSize;
	private boolean returnedLF = true;
	private boolean eos = false;

	public OneLineToMultiLineInputStreamWrapper(InputStream in, int lineSize) {
		super(in);
		this.lineSize = lineSize;
	}

	@Override
	public int read() throws IOException {
		if (eos) {
			return -1;
		}
		if (!returnedLF && readSize % lineSize == 0) {
			returnedLF = true;
			return LF;
		}
		returnedLF = false;
		int i = super.read();
		if (i == -1) {
			eos = true;
			return i;
		}
		readSize++;
		return i;
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		for (int i = 0; i < len; i++) {
			int r = read();
			if (r == -1) {
				if (i == 0) {
					return -1;
				}
				return i;
			}
			b[off + i] = (byte) r;
		}
		return len;
	}
}
