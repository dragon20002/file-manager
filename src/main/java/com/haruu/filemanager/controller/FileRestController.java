package com.haruu.filemanager.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	/**
	 * 작업위치 변경
	 * 
	 * @param depth 변경 대상 작업위치 레벨
	 * @param dirName 변경 대상 작업위치명
	 * @param request HttpRequest
	 * @return
	 */
	@GetMapping("/api/dir/{depth}")
	public ResponseEntity<Map<String, Object>> getDir(@PathVariable int depth, HttpServletRequest request) {
		return getDir(depth, "", request);
	}

	/**
	 * 작업위치 변경
	 * 
	 * @param depth 변경 대상 작업위치 레벨
	 * @param dirName 변경 대상 작업위치명
	 * @param request HttpRequest
	 * @return
	 */
	@GetMapping("/api/dir/{depth}/{dirName}")
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
		List<FileInfo> fileInfos = fileService.findAll(FileService.DIR_NAME, dirPath, null);
		if (request.isUserInRole("ADMIN"))
			fileInfos.addAll(fileService.findAll(FileService.SAFE_DIR_NAME, dirPath, null));

		Map<String, Object> data = new HashMap<>();
		data.put("depth", depth);
		data.put("dirPath", dirPath);
		data.put("fileInfos", fileInfos);

		return new ResponseEntity<>(data, HttpStatus.OK);
	}

	/**
	 * 작업위치 추가
	 * 
	 * @param dirName 추가 대상 작업위치명 
	 * @param request HttpRequest
	 * @return
	 */
	@PostMapping("/api/dir/" + FileService.DIR_NAME)
	public ResponseEntity<FileInfo> postDir(@RequestParam String dirName, HttpServletRequest request) {
		HttpSession session = request.getSession();
		String dirPath = Session.getDirPath(session);

		// 디렉터리 생성
		FileInfo dirInfo = fileService.generateDir(FileService.DIR_NAME, dirPath, dirName);
		if (dirInfo != null)
			return new ResponseEntity<>(dirInfo, HttpStatus.CREATED); //생성 성공
		return new ResponseEntity<>(HttpStatus.NO_CONTENT); //생성 실패
	}

	/**
	 * Safe 작업위치 생성
	 * 
	 * @param dirName 생성 대상 작업위치명
	 * @param request HttpRequest
	 * @return
	 */
	@PostMapping("/api/dir/" + FileService.SAFE_DIR_NAME)
	public ResponseEntity<FileInfo> postSafeDir(@RequestParam String dirName, HttpServletRequest request) {
		HttpSession session = request.getSession();
		String dirPath = Session.getDirPath(session);

		if (!request.isUserInRole("ADMIN"))
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);

		// 디렉터리 생성
		FileInfo dirInfo = fileService.generateDir(FileService.SAFE_DIR_NAME, dirPath, dirName);
		if (dirInfo != null)
			return new ResponseEntity<>(dirInfo, HttpStatus.CREATED); //생성 성공
		return new ResponseEntity<>(HttpStatus.NO_CONTENT); //생성 실패
	}

	/**
	 * 파일 목록
	 * 
	 * @param request HttpRequest
	 * @return
	 */
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

	/**
	 * 파일 정보
	 * 
	 * @param fileName 파일명
	 * @param request HttpRequest
	 * @return
	 */
	@GetMapping("/api/files/" + FileService.DIR_NAME + "/{fileName}")
	public ResponseEntity<FileInfo> getFile(@PathVariable String fileName, HttpServletRequest request) {
		HttpSession session = request.getSession();
		String dirPath = Session.getDirPath(session);

		// 파일정보
		FileInfo fileInfo = fileService.findOne(FileService.DIR_NAME, dirPath, fileName);
		if (fileInfo == null)
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);

		return new ResponseEntity<>(fileInfo, HttpStatus.OK);
	}

	/**
	 * Safe 파일 정보
	 * 
	 * @param fileName 파일명
	 * @param request HttpRequest
	 * @return
	 */
	@GetMapping("/api/files/" + FileService.SAFE_DIR_NAME + "/{fileName}")
	public ResponseEntity<FileInfo> getSafeFile(@PathVariable String fileName, HttpServletRequest request) {
		HttpSession session = request.getSession();
		String dirPath = Session.getDirPath(session);

		// 권한 확인
		if (!request.isUserInRole("ADMIN"))
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);

		// 파일정보
		FileInfo fileInfo = fileService.findOne(FileService.SAFE_DIR_NAME, dirPath, fileName);
		if (fileInfo == null)
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);

		return new ResponseEntity<>(fileInfo, HttpStatus.OK);
	}

	/**
	 * 파일 업로드
	 * 
	 * @param files 업로드 파일 목록
	 * @param request HttpRequest
	 * @return
	 */
	@PostMapping("/api/files/" + FileService.DIR_NAME)
	public ResponseEntity<List<FileInfo>> postFile(@RequestParam MultipartFile[] files, HttpServletRequest request) {
		HttpSession session = request.getSession();
		String dirPath = Session.getDirPath(session);

		try {
			// 파일 업로드
			List<FileInfo> fileInfos = fileService.saveAll(FileService.DIR_NAME, dirPath, files);
			log.debug("{} files saved", fileInfos.size());
			return new ResponseEntity<>(fileInfos, HttpStatus.CREATED); //업로드 성공
		} catch (IllegalStateException | IOException e) {
			log.debug(e.getMessage());
			return new ResponseEntity<>(HttpStatus.NO_CONTENT); //업로드 실패
		}
	}

	/**
	 * Safe 파일 업로드
	 * 
	 * @param files 업로드 파일 목록
	 * @param request HttpRequest
	 * @return
	 */
	@PostMapping("/api/files/" + FileService.SAFE_DIR_NAME)
	public ResponseEntity<List<FileInfo>> postSafeFile(@RequestParam MultipartFile[] files, HttpServletRequest request) {
		HttpSession session = request.getSession();
		String dirPath = Session.getDirPath(session);

		if (!request.isUserInRole("ADMIN"))
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);

		try {
			// 파일 업로드
			List<FileInfo> fileInfos = fileService.saveAll(FileService.SAFE_DIR_NAME, dirPath, files);
			log.debug("{} files saved", fileInfos.size());
			return new ResponseEntity<>(fileInfos, HttpStatus.CREATED); //업로드 성공
		} catch (IllegalStateException | IOException e) {
			log.debug(e.getMessage());
			return new ResponseEntity<>(HttpStatus.NO_CONTENT); //업로드 실패
		}
	}

	@DeleteMapping("/api/files/" + FileService.DIR_NAME + "/{fileName}")
	public ResponseEntity<Void> deleteFile(@PathVariable String fileName, HttpServletRequest request) {
		HttpSession session = request.getSession();
		String dirPath = Session.getDirPath(session);

		if (fileService.delete(FileService.DIR_NAME, dirPath, fileName))
			return new ResponseEntity<>(HttpStatus.OK); //삭제 성공
		return new ResponseEntity<>(HttpStatus.NO_CONTENT); //삭제 실패
	}

	@DeleteMapping("/api/files/" + FileService.SAFE_DIR_NAME + "/{fileName}")
	public ResponseEntity<Void> deleteSafeFile(@PathVariable String fileName, HttpServletRequest request) {
		HttpSession session = request.getSession();
		String dirPath = Session.getDirPath(session);

		if (request.isUserInRole("ADMIN") && fileService.delete(FileService.SAFE_DIR_NAME, dirPath, fileName))
			return new ResponseEntity<>(HttpStatus.OK); //삭제 성공
		return new ResponseEntity<>(HttpStatus.NO_CONTENT); //삭제 실패
	}

}
