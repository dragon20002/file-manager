package com.haruu.filemanager.controller;

import java.io.FileFilter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.haruu.filemanager.model.FileInfo;
import com.haruu.filemanager.service.FileService;
import com.haruu.filemanager.util.Session;
import com.haruu.filemanager.util.Common.Mp4FileFilter;

@RestController
public class Mp4RestController {

	@Autowired
	FileService fileService;

	@GetMapping("/api/mp4-files")
	public ResponseEntity<List<FileInfo>> getFiles(HttpServletRequest request) {
		HttpSession session = request.getSession();
		String dirPath = Session.getDirPath(session);

		// Mp4 파일정보 목록
		FileFilter fileFilter = new Mp4FileFilter();
		List<FileInfo> fileInfos = fileService.findAll(FileService.DIR_NAME, dirPath, fileFilter);
		if (request.isUserInRole("ADMIN"))
			fileInfos.addAll(fileService.findAll(FileService.SAFE_DIR_NAME, dirPath, fileFilter));

		return new ResponseEntity<>(fileInfos, HttpStatus.OK);
	}

	@GetMapping("/api/mp4-files/{rootDirName}/{fileName}")
	public ResponseEntity<FileInfo> getFile(@PathVariable String rootDirName, @PathVariable String fileName, HttpServletRequest request) {
		HttpSession session = request.getSession();
		String dirPath = Session.getDirPath(session);

		// Mp4 파일정보
		FileInfo fileInfo = fileService.findOne(rootDirName, dirPath, fileName);
		if (fileInfo == null)
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);

		// 자막
		fileService.generateVttSubtitle(rootDirName, dirPath, fileName);

		// 썸네일
		fileService.generateThumbnail(rootDirName, dirPath, fileName);

		return new ResponseEntity<>(fileInfo, HttpStatus.OK);
	}

}
