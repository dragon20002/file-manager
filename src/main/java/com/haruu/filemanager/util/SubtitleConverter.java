package com.haruu.filemanager.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubtitleConverter {
	private static Logger log = LoggerFactory.getLogger(SubtitleConverter.class);

	/**
	 * 변환은 잘 되는데 브라우저에서 자막파일 읽을 때 EUC-KR을 인식 못함 
	 */
	public static File convertSmiToVtt(File smiFile, File vttFile) {
		long lineNum = 0;
		Pattern syncTagPtn = Pattern.compile("sync start=[0-9]+");
		Pattern tsPtn = Pattern.compile("[0-9]+"); //timestamp pattern
		Pattern pClassPtn = Pattern.compile("<p class=.*>");

		String charset = CharsetDetector.getCharset(smiFile);
		log.debug("CHAR-SET = " + charset);

		try {
			vttFile.createNewFile();
			PrintWriter writer = new PrintWriter(
					new OutputStreamWriter(
							new FileOutputStream(vttFile),
							charset)
					);
			writer.println(decode("WEBVTT\n\n", charset));

			BufferedReader reader = new BufferedReader(
					new InputStreamReader(
							new FileInputStream(smiFile),
							charset)
					);
			String startLine = reader.readLine();
			while (startLine != null) { //한 줄씩 읽기
				if (syncTagPtn.matcher(startLine.toLowerCase()).find()) {
					// Start Timestamp
					Matcher startTsMtr = tsPtn.matcher(startLine);
					if (!startTsMtr.find()) {
						log.debug("잘못된 형식의 자막파일 입니다.");
						break;
					}
					long startTs = Long.parseLong(startTsMtr.group());
					
					// 자막 읽기
					long linePos = lineNum + 1; //현재 읽고 있는 줄 번호
					String text = startLine; //자막 내용

					String nextLine = reader.readLine();
					while (nextLine != null && !syncTagPtn.matcher(nextLine.toLowerCase()).find()) {
						text += nextLine;
						linePos++;
						nextLine = reader.readLine();
					}

					if (nextLine == null) {
						log.debug("SMI2VTT 변환 종료");
						break;
					}

					// End Timestamp
					Matcher endTsMtr = tsPtn.matcher(nextLine);
					if (!endTsMtr.find()) {
						log.debug("잘못된 형식의 자막파일 입니다.");
						break;
					}
					long endTs = Long.parseLong(endTsMtr.group());

					// VTT 자막 파일 쓰기
					if (!text.isEmpty()) {
						text = text.replace("\n", "")
								.replace("<br>", "\n")
								.replaceAll(".*<P", "<P")
								.replaceAll("\n\n/|\n", "\n")
								.replaceAll("<b>|</b>|&nbsp;", "")
								.replaceAll("<\\/.+>", "")
								.replaceAll("<.+>", "");

						Matcher pClassMtr = pClassPtn.matcher(text);
						if (pClassMtr.find()) {
							lineNum = linePos;
							startLine = nextLine;
							continue;
						}

						if (!text.isEmpty()) {
							writer.println(decode(tsToTime(startTs) + " --> " + tsToTime(endTs), charset));
							writer.println(decode(text, charset));
							writer.println();
						}
					}

					lineNum = linePos;
					startLine = nextLine;
				} else {
					lineNum++;
					startLine = reader.readLine();
				}

			}

			reader.close();
			writer.close();
		} catch (IOException e) {
			log.debug("IOException");
		}

		return vttFile;
	}

	private static String tsToTime(long timestamp) {
		int millis = (int) (timestamp % 1000);
		int sec = (int) (timestamp / 1000);
		int min = sec / 60;
		sec %= 60;
		int hour = min / 60;
		min %= 60;
		
		return String.format("%02d:%02d:%02d.%03d", hour, min, sec, millis);
	}

	private static String decode(String input, String charset) {
		// TODO - charset 변경
		return input;
	}

}
