package com.haruu.filemanager.controller;

import java.io.FileFilter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.haruu.filemanager.model.FileInfo;
import com.haruu.filemanager.service.FileService;
import com.haruu.filemanager.util.Common;
import com.haruu.filemanager.util.Session;
import com.mpatric.mp3agic.ID3v2;

@RestController
public class Mp3RestController {
	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	FileService fileService;

	@GetMapping("/api/mp3-files")
	public ResponseEntity<List<FileInfo>> getFiles(HttpServletRequest request) {
		HttpSession session = request.getSession();
		String dirPath = Session.getDirPath(session);

		// Mp3 파일정보 목록
		FileFilter fileFilter = new Common.Mp3FileFilter();
		List<FileInfo> fileInfos = fileService.findAll(FileService.DIR_NAME, dirPath, fileFilter);
		if (request.isUserInRole("ADMIN"))
			fileInfos.addAll(fileService.findAll(FileService.SAFE_DIR_NAME, dirPath, fileFilter));

		return new ResponseEntity<>(fileInfos, HttpStatus.OK);
	}

	@GetMapping("/api/mp3-files/{rootDirName}/{fileName}")
	public ResponseEntity<FileInfo> getFile(@PathVariable String rootDirName, @PathVariable String fileName, HttpServletRequest request) {
		HttpSession session = request.getSession();
		String dirPath = Session.getDirPath(session);

		// Mp3 파일정보
		FileInfo fileInfo = fileService.findOne(rootDirName, dirPath, fileName);
		if (fileInfo == null)
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);

		return new ResponseEntity<>(fileInfo, HttpStatus.OK);
	}

	@GetMapping("/api/mp3-files/{rootDirName}/{fileName}/id3")
	public ResponseEntity<ID3v2> getId3(@PathVariable String rootDirName, @PathVariable String fileName, HttpServletRequest request) {
		HttpSession session = request.getSession();
		String dirPath = Session.getDirPath(session);

		ID3v2 id3;
		try {
			id3 = fileService.generateMp3Id3Tag(rootDirName, dirPath, fileName);
			if (id3 != null)
				return new ResponseEntity<>(id3, HttpStatus.OK);
		} catch (Exception e) {
			log.debug(e.getMessage());
		}
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

}
