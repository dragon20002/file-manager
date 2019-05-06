package com.haruu.filemanager.service;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService {
	public static final String DIR_NAME = "files/";

	@Autowired
	private ServletContext context;

	public void saveAll(MultipartFile[] files) throws IllegalStateException, IOException {
		String realPath = context.getRealPath("/") + DIR_NAME;
		makeDirectory(realPath);

		for (MultipartFile file : files) {
			if (file == null || file.isEmpty() || isUnSafe(file.getOriginalFilename()))
				continue;

			String filename = file.getOriginalFilename();
			file.transferTo(new File(realPath + filename));
		}
	}

	private boolean isUnSafe(String filename) {
		return Pattern.compile("/(\\/\\.\\.)|(\\.\\.\\/)|(\\\\\\.\\.)|(\\.\\.\\\\)/").matcher(filename).matches();
	}

	private boolean makeDirectory(String path) {
		File dir = new File(path);
		if (!dir.exists() && !dir.mkdirs())
			return false;
		return true;
	}

	public File[] findAll() {
		String realPath = context.getRealPath("/") + DIR_NAME;
		File directory = new File(realPath);
		return (directory.exists()) ? directory.listFiles() : new File[0];
	}
}
