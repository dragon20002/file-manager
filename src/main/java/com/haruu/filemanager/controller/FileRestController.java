package com.haruu.filemanager.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.haruu.filemanager.model.FileInfo;
import com.haruu.filemanager.service.FileService;

@RestController
public class FileRestController {

	private Logger log = LoggerFactory.getLogger(FileRestController.class);

	@Autowired
	private FileService fileService;

	@GetMapping("/api/files")
	public ResponseEntity<List<FileInfo>> getFiles(HttpServletRequest request) {
		// 파일정보 목록
		List<FileInfo> fileInfos = fileService.findAll(FileService.DIR_NAME, "", null);
		if (request.isUserInRole("ADMIN"))
			fileInfos.addAll(fileService.findAll(FileService.SAFE_DIR_NAME, "", null));

		return new ResponseEntity<>(fileInfos, HttpStatus.OK);
	}

	@GetMapping("/api/files/{rootDirName}/{fileName}")
	public ResponseEntity<FileInfo> getFile(@PathVariable String rootDirName, @PathVariable String fileName, HttpServletRequest request) {
		// 권한 확인
		if (rootDirName.equals(FileService.SAFE_DIR_NAME) && !request.isUserInRole("ADMIN"))
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		// 파일정보
		FileInfo fileInfo = fileService.findOne(rootDirName, "", fileName);
		if (fileInfo == null)
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);

		return new ResponseEntity<>(fileInfo, HttpStatus.OK);
	}

	@PostMapping("/api/files/{rootDirName}")
	public ResponseEntity<List<FileInfo>> postFile(@PathVariable String rootDirName, @RequestParam(defaultValue = "") String dirPath, @RequestParam MultipartFile[] files) {
		try {
			// 파일 업로드
			List<FileInfo> fileInfos = fileService.saveAll(rootDirName, dirPath, files);
			log.debug("{} files saved", fileInfos.size());
			return new ResponseEntity<>(fileInfos, HttpStatus.CREATED); //업로드 성공
		} catch (IllegalStateException | IOException e) {
			log.debug(e.getMessage());
			return new ResponseEntity<>(HttpStatus.NO_CONTENT); //업로드 실패
		}
	}

	@DeleteMapping("/api/files/{rootDirName}/{fileName}")
	public ResponseEntity<Void> deleteFile(@PathVariable String rootDirName, @PathVariable String fileName, HttpServletRequest request) {
		if (request.isUserInRole("ADMIN") && fileService.delete(rootDirName, "", fileName))
			return new ResponseEntity<>(HttpStatus.OK); //삭제 성공
		return new ResponseEntity<>(HttpStatus.NO_CONTENT); //삭제 실패
	}

}
