package com.haruu.filemanager.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.haruu.filemanager.model.FileInfo;
import com.haruu.filemanager.util.Common;
import com.haruu.filemanager.util.SubtitleConverter;

@Service
public class FileService {
	public static final String DIR_NAME = "files/";
	public static final String SAFE_DIR_NAME = "safe/";
	public static final String VIDEOS = "videos/";

	@Autowired
	private ServletContext context;

	/* file upload */
	public String saveAll(MultipartFile[] files) throws IllegalStateException, IOException {
		String realPath = context.getRealPath("/") + DIR_NAME;
		makeDirectory(realPath);

		for (MultipartFile file : files) {
			if (file == null || file.isEmpty() || isUnSafe(file.getOriginalFilename()))
				continue;

			String filename = file.getOriginalFilename();
			file.transferTo(new File(realPath + filename));
		}

		return realPath;
	}

	private boolean isUnSafe(String filename) {
		// /.. or ../ or \.. or ..\
		return Pattern.compile("/(\\/\\.\\.)|(\\.\\.\\/)|(\\\\\\.\\.)|(\\.\\.\\\\)/").matcher(filename).matches();
	}

	private boolean makeDirectory(String path) {
		File dir = new File(path);
		if (!dir.exists() && !dir.mkdirs())
			return false;
		return true;
	}

	/* search */
	// 파일목록 조회
	public File[] findAll(int safe) {
		return findAll(safe, "");
	}

	public File[] findAll(int safe, String dirname) {
		String rootDirName = (safe != 1) ? DIR_NAME : SAFE_DIR_NAME; 
		String realPath = context.getRealPath("/") + rootDirName + dirname;
		File directory = new File(realPath);
		File[] files = (directory.exists()) ? directory.listFiles() : new File[0];
		return files;
	}

	// 파일정보 조회
	public List<FileInfo> getFileInfoList(File[] files) {
		List<FileInfo> fileInfoList = new ArrayList<>();
		for (File file : files) {
			if (file.length() == 0 || file.isDirectory()) continue;
			FileInfo fileInfo = new FileInfo(
					file.getName(),
					Common.rawUrlEncode(file.getName()),
					file.length() / 1024,
					file.getName().matches(".*(\\.mp3)"));
			fileInfoList.add(fileInfo);
		}
		return fileInfoList;
	}
	
	public List<FileInfo> getVideoInfoList(File[] files) {
		List<FileInfo> videoInfoList = new ArrayList<>();
		for (File file : files) {
			if (file.length() == 0 || file.isDirectory() || file.getName().matches(".*((\\.smi)|(\\.srt)|(\\.vtt))")) continue;
			FileInfo fileInfo = new FileInfo(
					file.getName(),
					Common.rawUrlEncode(file.getName()),
					file.length() / 1024,
					true);
			videoInfoList.add(fileInfo);
		}
		return videoInfoList;
	}

	/* create */
	public void convertSubtitle(int safe, String subName) {
		String rootDirName = (safe != 1) ? DIR_NAME : SAFE_DIR_NAME;
		String realPath = context.getRealPath("/") + rootDirName + VIDEOS;
		SubtitleConverter.convertSmiToVtt(realPath, subName);
	}

}
