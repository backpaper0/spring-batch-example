package com.example.common;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.junit.jupiter.api.Test;

public class OneLineToMultiLineInputStreamWrapperTest {

	@Test
	void testRead() throws Exception {
		byte[] buf = "foobarbaz".getBytes();
		InputStream in = new ByteArrayInputStream(buf);
		int lineSize = 3;
		OneLineToMultiLineInputStreamWrapper sut = new OneLineToMultiLineInputStreamWrapper(in, lineSize);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int b;
		while (-1 != (b = sut.read())) {
			out.write(b & 0xff);
		}
		assertEquals("foo\nbar\nbaz\n", out.toString());

		assertEquals(-1, sut.read());
		assertEquals(-1, sut.read());
		assertEquals(-1, sut.read());
		assertEquals(-1, sut.read());
	}

	@Test
	void testReadBytes1() throws Exception {
		byte[] buf = "foobarbaz".getBytes();
		InputStream in = new ByteArrayInputStream(buf);
		int lineSize = 3;
		OneLineToMultiLineInputStreamWrapper sut = new OneLineToMultiLineInputStreamWrapper(in, lineSize);
		byte[] b = new byte[100];
		int i = sut.read(b, 0, b.length);
		String expected = "foo\nbar\nbaz\n";
		assertEquals(expected.length(), i);
		assertEquals(expected, new String(b, 0, i));
	}

	@Test
	void testReadBytes2() throws Exception {
		byte[] buf = "foobarbaz".getBytes();
		InputStream in = new ByteArrayInputStream(buf);
		int lineSize = 3;
		OneLineToMultiLineInputStreamWrapper sut = new OneLineToMultiLineInputStreamWrapper(in, lineSize);
		byte[] b = new byte[3];
		int i = sut.read(b, 0, b.length);
		assertEquals(i, 3);
		assertEquals("foo", new String(b, 0, 3));

		i = sut.read(b, 0, b.length);
		assertEquals(i, 3);
		assertEquals("\nba", new String(b, 0, 3));

		i = sut.read(b, 0, b.length);
		assertEquals(i, 3);
		assertEquals("r\nb", new String(b, 0, 3));

		i = sut.read(b, 0, b.length);
		assertEquals(i, 3);
		assertEquals("az\n", new String(b, 0, 3));

		i = sut.read(b, 0, b.length);
		assertEquals(i, -1);
		assertEquals("az\n", new String(b, 0, 3));
	}

	@Test
	void testReadBytes3() throws Exception {
		byte[] buf = "foobarbaz".getBytes();
		InputStream in = new ByteArrayInputStream(buf);
		int lineSize = 3;
		OneLineToMultiLineInputStreamWrapper sut = new OneLineToMultiLineInputStreamWrapper(in, lineSize);
		byte[] b = new byte[5];
		int i = sut.read(b, 0, b.length);
		assertEquals(i, 5);
		assertEquals("foo\nb", new String(b, 0, 5));

		i = sut.read(b, 0, b.length);
		assertEquals(i, 5);
		assertEquals("ar\nba", new String(b, 0, 5));

		i = sut.read(b, 0, b.length);
		assertEquals(i, 2);
		assertEquals("z\n", new String(b, 0, 2));

		i = sut.read(b, 0, b.length);
		assertEquals(i, -1);
		assertEquals("z\n", new String(b, 0, 2));
	}

	@Test
	void testReadAllBytes() throws Exception {
		byte[] buf = "foobarbaz".getBytes();
		InputStream in = new ByteArrayInputStream(buf);
		int lineSize = 3;
		OneLineToMultiLineInputStreamWrapper sut = new OneLineToMultiLineInputStreamWrapper(in, lineSize);
		assertArrayEquals("foo\nbar\nbaz\n".getBytes(), sut.readAllBytes());
	}
}
