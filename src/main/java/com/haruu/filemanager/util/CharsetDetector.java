package com.haruu.filemanager.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class CharsetDetector {
	private static String DEFAULT_CHARSET = "UTF-8";
	
	public static String getCharset(File file) {
		String[] names = { "UTF-8", "UTF-16", "ISO-8859-7", "ISO-8859-1", "ISO-8859-2", "ISO-8859-4", "US-ASCII",
				"windows-1250", "windows-1251", "windows-1252", "windows-1253", "windows-1254", "windows-1257",
				"UTF-16BE", "UTF-32", "UTF-16LE", "UTF-32BE", "UTF-32LE", "ISO-8859-5", "ISO-8859-7", "ISO-8859-9",
				"ISO-8859-13", "ISO-8859-15", "x-UTF-32BE-BOM", "x-UTF-32LE-BOM", "x-UTF-16LE-BOM", "x-IBM874",
				"x-IBM737", "IBM00858", "IBM437", "IBM775", "IBM850", "IBM852", "IBM855", "IBM857", "IBM862", "IBM866",
				"KOI8-R", "KOI8-U" };

		String result = "UTF-8";
		for (String name : names) {
			Charset charset = Charset.forName(name);
			if (detect(file, charset)) {
				result = name;
				break;
			}
		}

		// 한번 더 테스트
		if (result.contentEquals(DEFAULT_CHARSET)) {
			BufferedInputStream input;
			try {
				input = new BufferedInputStream(new FileInputStream(file));
				byte[] buffer = new byte[1024];
				input.read(buffer);
				result = isEucKr(buffer);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	private static boolean detect(File file, Charset charset) {
		boolean succ = false;

		BufferedInputStream input;
		try {
			input = new BufferedInputStream(new FileInputStream(file));
			CharsetDecoder decoder = charset.newDecoder();
			decoder.reset();

			byte[] buffer = new byte[1024];
			while (input.read(buffer) != -1) {
				try {
					decoder.decode(ByteBuffer.wrap(buffer));
					succ = true;
					break;
				} catch (CharacterCodingException e) {
					//try more
				}
			}

			input.close();

		} catch (IOException e) {
			e.printStackTrace();
			succ = false;
		}
		
		return succ;
	}

	private static String isEucKr(byte[] buffer) {
		String result = "EUC-KR"; //기본 charset을 테스트해보고 아니면 EUC-KR

		CharsetDecoder decoder = Charset.forName(DEFAULT_CHARSET).newDecoder();
		try {
			CharBuffer chBuf = decoder.decode(ByteBuffer.wrap(buffer));
			result = chBuf.toString();
		} catch (CharacterCodingException e) {
			//EUC-KR
		}

		return result;
	}
	
}
