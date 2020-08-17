package com.haruu.filemanager.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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
import com.haruu.filemanager.util.Session;

@RestController
public class FileRestController {

	private Logger log = LoggerFactory.getLogger(FileRestController.class);

	@Autowired
	private FileService fileService;

	@GetMapping("/api/cd/{rootDirName}/{depth}/{dirName}")
	public ResponseEntity<List<FileInfo>> getDir(@PathVariable String rootDirName, @PathVariable Integer depth, @PathVariable String dirName, HttpServletRequest request) {
		HttpSession session = request.getSession();

		Integer oldDepth = Session.getDepth(session);
		String oldDirPath = Session.getDirPath(session);

		String dirPath = "";

		if (oldDepth == depth - 1) { //하위폴더로 이동
			dirPath = oldDirPath + "/" + dirName;
		} else if (oldDepth == depth + 1) { //상위폴더로 이동
			String[] toks = oldDirPath.split("/");
			for (int i = 0; i < toks.length - 1; i++) {
				dirPath += ("/" + toks[i]);
			}
		} else { //세션에 저장된 dirPath와 클라이언트 dirPath 불일치
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}

		Session.setDepth(session, depth);
		Session.setDirPath(session, dirPath);

		// 파일정보 목록
		List<FileInfo> fileInfos = fileService.findAll(FileService.DIR_NAME, "", null);
		if (request.isUserInRole("ADMIN"))
			fileInfos.addAll(fileService.findAll(FileService.SAFE_DIR_NAME, "", null));

		return new ResponseEntity<>(fileInfos, HttpStatus.OK);
	}

	@PostMapping("/api/cd/{rootDirName}")
	public ResponseEntity<List<FileInfo>> postDir(@PathVariable String rootDirName, @RequestParam(defaultValue = "1") Integer depth, @RequestParam(defaultValue = "") String dirName) {
		// 디렉터리 생성
		if (fileService.generateDir(rootDirName, "", dirName))
			return new ResponseEntity<>(HttpStatus.CREATED); //생성 성공
		return new ResponseEntity<>(HttpStatus.NO_CONTENT); //생성 실패
	}

	@GetMapping("/api/files")
	public ResponseEntity<List<FileInfo>> getFiles(HttpServletRequest request) {
		HttpSession session = request.getSession();
		String dirPath = Session.getDirPath(session);

		// 파일정보 목록
		List<FileInfo> fileInfos = fileService.findAll(FileService.DIR_NAME, dirPath, null);
		if (request.isUserInRole("ADMIN"))
			fileInfos.addAll(fileService.findAll(FileService.SAFE_DIR_NAME, dirPath, null));

		return new ResponseEntity<>(fileInfos, HttpStatus.OK);
	}

	@GetMapping("/api/files/{rootDirName}/{fileName}")
	public ResponseEntity<FileInfo> getFile(@PathVariable String rootDirName, @PathVariable String fileName, HttpServletRequest request) {
		HttpSession session = request.getSession();
		String dirPath = Session.getDirPath(session);

		// 권한 확인
		if (rootDirName.equals(FileService.SAFE_DIR_NAME) && !request.isUserInRole("ADMIN"))
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		// 파일정보
		FileInfo fileInfo = fileService.findOne(rootDirName, dirPath, fileName);
		if (fileInfo == null)
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);

		return new ResponseEntity<>(fileInfo, HttpStatus.OK);
	}

	@PostMapping("/api/files/{rootDirName}")
	public ResponseEntity<List<FileInfo>> postFile(@PathVariable String rootDirName, @RequestParam MultipartFile[] files, HttpServletRequest request) {
		HttpSession session = request.getSession();
		String dirPath = Session.getDirPath(session);

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
		HttpSession session = request.getSession();
		String dirPath = Session.getDirPath(session);

		if (request.isUserInRole("ADMIN") && fileService.delete(rootDirName, dirPath, fileName))
			return new ResponseEntity<>(HttpStatus.OK); //삭제 성공
		return new ResponseEntity<>(HttpStatus.NO_CONTENT); //삭제 실패
	}

}
