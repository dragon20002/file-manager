package com.haruu.filemanager.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Pattern;

import org.springframework.web.multipart.MultipartFile;

import com.haruu.filemanager.model.FileInfo;

public class Common {

	/* ---------- FILE ---------- */

	/**
	 * Convert to FileInfo
	 */
	public static FileInfo getFileInfo(File file) {
		if (file == null) return null;

		String rootDirName = file.getParent();
		rootDirName = rootDirName.substring(rootDirName.lastIndexOf('\\') + 1);

		return new FileInfo(
				file.getName(),
				file.length() / 1024,
				rootDirName,
				encodeURI(file.getName()),
				file.isDirectory());
	}

	/**
	 * JS encodeURI
	 */
	public static String encodeURI(String src) {
		String dest = null;
		try {
			dest = URLEncoder.encode(src, "UTF-8")
					.replaceAll("\\+", "%20")
					.replaceAll("\\%21", "!")
					.replaceAll("\\%27", "'")
					.replaceAll("\\%28", "(")
					.replaceAll("\\%29", ")")
					.replaceAll("\\%7E", "~");
		} catch (UnsupportedEncodingException e) {
			dest = src;
		}
		return dest;
	}

	/**
	 * Save received file
	 * 
	 * @param mpFile uploaded file
	 * @param realDirPath file path to save
	 * @return saved file
	 */
	public static File save(MultipartFile mpFile, String realDirPath) throws IllegalStateException, IOException {
		File file = new File(realDirPath + "/" + mpFile.getOriginalFilename());
		mpFile.transferTo(file);
		return file;
	}

	/* ---------- REGEX ----------- */
	
	private static Pattern unsafeFilenamePtn = Pattern.compile("/(\\/\\.\\.)|(\\.\\.\\/)|(\\\\\\.\\.)|(\\.\\.\\\\)/"); 

	/**
	 * Check unsafe file name
	 * 
	 * @param file name to check
	 * @return if unsafe true else false
	 */
	public static boolean isUnSafe(String filename) {
		// /.. or ../ or \.. or ..\
		return unsafeFilenamePtn.matcher(filename).matches();
	}

	/* ---------- File Filter ---------- */

	/**
	 * 'mp3' Extension file filter
	 * 
	 * @author haruu
	 */
	public static class Mp3FileFilter implements FileFilter {

		@Override
		public boolean accept(File pathname) {
			return pathname.getName().endsWith(".mp3");
		}
	}

	/**
	 * 'mp4' Extension file filter
	 * 
	 * @author haruu
	 */
	public static class Mp4FileFilter implements FileFilter {

		@Override
		public boolean accept(File pathname) {
			String name = pathname.getName();
			return name.endsWith(".mp4") || name.endsWith(".avi") || name.endsWith(".mkv");
		}
	}
	
}
