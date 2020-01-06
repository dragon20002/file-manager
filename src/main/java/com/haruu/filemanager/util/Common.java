package com.haruu.filemanager.util;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import org.springframework.web.multipart.MultipartFile;

import com.haruu.filemanager.model.FileInfo;

public class Common {
	
	/* Convert to FileInfo */
	public static FileInfo getFileInfo(File file) {
		if (file == null) return null;

		String rootDirName = file.getParent();
		rootDirName = rootDirName.substring(rootDirName.lastIndexOf('\\') + 1);

		return new FileInfo(
				file.getName(),
				file.length() / 1024,
				rootDirName);
	}

	/* Save received file */
	public static File save(MultipartFile mpFile, String realDirPath) throws IllegalStateException, IOException {
		File file = new File(realDirPath + "/" + mpFile.getOriginalFilename());
		mpFile.transferTo(file);
		return file;
	}

	/* REGEX */
	private static Pattern unsafeFilenamePtn = Pattern.compile("/(\\/\\.\\.)|(\\.\\.\\/)|(\\\\\\.\\.)|(\\.\\.\\\\)/"); 

	public static boolean isUnSafe(String filename) {
		// /.. or ../ or \.. or ..\
		return unsafeFilenamePtn.matcher(filename).matches();
	}

}
