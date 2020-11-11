package com.haruu.filemanager.controller;

import java.io.FileFilter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.haruu.filemanager.model.FileInfo;
import com.haruu.filemanager.service.FileService;
import com.haruu.filemanager.util.Common;
import com.mpatric.mp3agic.ID3v2;

@RestController
public class Mp3RestController {
	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	FileService fileService;

	@GetMapping("/api/mp3-files")
	public ResponseEntity<List<FileInfo>> getFiles(HttpServletRequest request) {
		// Mp3 파일정보 목록
		FileFilter fileFilter = new Common.Mp3FileFilter();
		List<FileInfo> fileInfos = fileService.findAllRecursive(FileService.DIR_NAME, "", fileFilter);
		if (request.isUserInRole("ADMIN"))
			fileInfos.addAll(fileService.findAllRecursive(FileService.SAFE_DIR_NAME, "", fileFilter));

		return new ResponseEntity<>(fileInfos, HttpStatus.OK);
	}

	@PostMapping("/api/mp3-files/" + FileService.DIR_NAME + "/{fileName}")
	public ResponseEntity<FileInfo> getFile(@PathVariable String fileName, @RequestParam String dirPath) {
		// Mp3 파일정보
		FileInfo fileInfo = fileService.findOne(FileService.DIR_NAME, dirPath, fileName);
		if (fileInfo == null)
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);

		return new ResponseEntity<>(fileInfo, HttpStatus.OK);
	}

	@PostMapping("/api/mp3-files/" + FileService.SAFE_DIR_NAME + "/{fileName}")
	public ResponseEntity<FileInfo> getSafeFile(@PathVariable String fileName, @RequestParam String dirPath, HttpServletRequest request) {
		if (!request.isUserInRole("ADMIN"))
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);

		// Mp3 파일정보
		FileInfo fileInfo = fileService.findOne(FileService.SAFE_DIR_NAME, dirPath, fileName);
		if (fileInfo == null)
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);

		return new ResponseEntity<>(fileInfo, HttpStatus.OK);
	}

	@PostMapping("/api/mp3-files/" + FileService.DIR_NAME + "/{fileName}/id3")
	public ResponseEntity<ID3v2> getId3(@PathVariable String fileName, @RequestParam String dirPath) {
		ID3v2 id3;
		try {
			id3 = fileService.generateMp3Id3Tag(FileService.DIR_NAME, dirPath, fileName);
			if (id3 != null)
				return new ResponseEntity<>(id3, HttpStatus.OK);
		} catch (Exception e) {
			log.debug(e.getMessage());
		}
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@PostMapping("/api/mp3-files/" + FileService.SAFE_DIR_NAME + "/{fileName}/id3")
	public ResponseEntity<ID3v2> getSafeId3(@PathVariable String fileName, @RequestParam String dirPath, HttpServletRequest request) {
		if (!request.isUserInRole("ADMIN"))
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);

		ID3v2 id3;
		try {
			id3 = fileService.generateMp3Id3Tag(FileService.SAFE_DIR_NAME, dirPath, fileName);
			if (id3 != null)
				return new ResponseEntity<>(id3, HttpStatus.OK);
		} catch (Exception e) {
			log.debug(e.getMessage());
		}
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

}
