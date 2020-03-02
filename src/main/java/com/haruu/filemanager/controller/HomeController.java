package com.haruu.filemanager.controller;

import java.io.FileFilter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.haruu.filemanager.model.FileInfo;
import com.haruu.filemanager.service.FileService;
import com.haruu.filemanager.util.Common.Mp4FileFilter;

@Controller
public class HomeController {
	private Logger log = LoggerFactory.getLogger(HomeController.class);

	@Autowired
	FileService fileService;
	
	@GetMapping("/")
	public String home(Model model, HttpServletRequest request) {
		String rootDirName = (request.isUserInRole("ADMIN")) ? FileService.SAFE_DIR_NAME : FileService.DIR_NAME;
		model.addAttribute("rootDirName", rootDirName);

		// csrfToken
		CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");
		log.debug("header = {}", csrfToken.getHeaderName());
		log.debug("param = {}", csrfToken.getParameterName());
		log.debug("token = {}", csrfToken.getToken());

		return "home";
	}
	
	@GetMapping("/nojs-home")
	public String nojsHome(Model model, HttpServletRequest request) {
		String rootDirName = (request.isUserInRole("ADMIN")) ? FileService.SAFE_DIR_NAME : FileService.DIR_NAME;
		model.addAttribute("rootDirName", rootDirName);

		// 파일정보 목록
		List<FileInfo> fileInfos = fileService.findAll(FileService.DIR_NAME, "", null);
		if (request.isUserInRole("ADMIN"))
			fileInfos.addAll(fileService.findAll(FileService.SAFE_DIR_NAME, "", null));
		model.addAttribute("fileInfos", fileInfos);

		// Mp4 파일정보 목록
		FileFilter videoFilter = new Mp4FileFilter();
		List<FileInfo> videoInfos = fileService.findAll(FileService.DIR_NAME, "", videoFilter);
		if (request.isUserInRole("ADMIN"))
			videoInfos.addAll(fileService.findAll(FileService.SAFE_DIR_NAME, "", videoFilter));
		model.addAttribute("videoInfos", videoInfos);

		return "nojs_home";
	}

	@GetMapping("/nojs-mp4/{rootDirName}/{fileName}")
	public String nojsMp4(Model model,
			@PathVariable String rootDirName, @PathVariable String fileName) {
		
		// Mp4 파일정보
		FileInfo fileInfo = fileService.findOne(rootDirName, "", fileName);
		if (fileInfo == null)
			return "error";

		// 자막
		fileService.generateVttSubtitle(rootDirName, "", fileName);

		// 썸네일
		fileService.generateThumbnail(rootDirName, "", fileName);

		model.addAttribute("video", fileInfo);
		
		String trackName = fileInfo.getEncName().substring(0, fileInfo.getEncName().lastIndexOf(".")) + ".vtt";

		model.addAttribute("trackName", trackName);

		return "nojs_watch_video";
	}
	
	@GetMapping("/mp3")
	public String mp3() {
		return "play_music";
	}

	@GetMapping("/mp4")
	public String mp4() {
		return "watch_video";
	}

	@GetMapping("/login")
	public String login() {
		return "login";
	}

	@GetMapping("/error")
	public String error() {
		return "error";
	}
}
