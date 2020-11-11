package com.haruu.filemanager.controller;

import java.io.FileFilter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.haruu.filemanager.util.Common.Mp4FileFilter;
import com.haruu.filemanager.util.Session;

@RestController
public class Mp4RestController {

	@Autowired
	FileService fileService;

	@GetMapping("/api/mp4-dir/{depth}")
	public ResponseEntity<Map<String, Object>> getDir(@PathVariable int depth, HttpServletRequest request) {
		return getDir(depth, "", request);
	}

	@GetMapping("/api/mp4-dir/{depth}/{dirName}")
	public ResponseEntity<Map<String, Object>> getDir(@PathVariable int depth, @PathVariable String dirName, HttpServletRequest request) {
		HttpSession session = request.getSession();
		int oldDepth = Session.getDepth(session);
		String oldDirPath = Session.getDirPath(session);

		String dirPath = "";
		if (oldDepth == depth - 1) { //하위폴더로 이동
			dirPath = oldDirPath + "/" + dirName;
		} else if (oldDepth == depth + 1) { //상위폴더로 이동
			String[] toks = oldDirPath.split("/");
			for (int i = 0; i < toks.length - 1; i++) {
				if (toks[i].length() > 0) {
					dirPath += ("/" + toks[i]);
				}
			}
		} else if (oldDepth != depth) { //세션에 저장된 dirPath와 클라이언트 dirPath 불일치
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			dirPath = oldDirPath;
		}

		Session.setDepth(session, depth);
		Session.setDirPath(session, dirPath);

		// 파일정보 목록
		FileFilter fileFilter = new Mp4FileFilter();
		List<FileInfo> fileInfos = fileService.findAll(FileService.DIR_NAME, dirPath, fileFilter);
		if (request.isUserInRole("ADMIN"))
			fileInfos.addAll(fileService.findAll(FileService.SAFE_DIR_NAME, dirPath, fileFilter));

		Map<String, Object> data = new HashMap<>();
		data.put("depth", depth);
		data.put("dirPath", dirPath);
		data.put("fileInfos", fileInfos);

		return new ResponseEntity<>(data, HttpStatus.OK);
	}

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

	@GetMapping("/api/mp4-files/" + FileService.DIR_NAME + "/{fileName}")
	public ResponseEntity<FileInfo> getFile(@PathVariable String fileName, HttpSession session) {
		String dirPath = Session.getDirPath(session);

		// Mp4 파일정보
		FileInfo fileInfo = fileService.findOne(FileService.DIR_NAME, dirPath, fileName);
		if (fileInfo == null)
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);

		// 자막
		fileService.generateVttSubtitle(FileService.DIR_NAME, dirPath, fileName);

		// 썸네일
		fileService.generateThumbnail(FileService.DIR_NAME, dirPath, fileName);

		return new ResponseEntity<>(fileInfo, HttpStatus.OK);
	}

	@GetMapping("/api/mp4-files/" + FileService.SAFE_DIR_NAME + "/{fileName}")
	public ResponseEntity<FileInfo> getSafeFile(@PathVariable String fileName, HttpServletRequest request) {
		String dirPath = Session.getDirPath(request.getSession());

		if (!request.isUserInRole("ADMIN"))
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);

		// Mp4 파일정보
		FileInfo fileInfo = fileService.findOne(FileService.SAFE_DIR_NAME, dirPath, fileName);
		if (fileInfo == null)
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);

		// 자막
		fileService.generateVttSubtitle(FileService.SAFE_DIR_NAME, dirPath, fileName);

		// 썸네일
		fileService.generateThumbnail(FileService.SAFE_DIR_NAME, dirPath, fileName);

		return new ResponseEntity<>(fileInfo, HttpStatus.OK);
	}

}
