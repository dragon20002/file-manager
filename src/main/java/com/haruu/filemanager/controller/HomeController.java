package com.haruu.filemanager.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.haruu.filemanager.service.FileService;

@Controller
public class HomeController {

	private Logger log = LoggerFactory.getLogger(HomeController.class);
	
	@GetMapping("/")
	public String home(Model model, HttpServletRequest request) {
		String rootDirName = (request.isUserInRole("ADMIN")) ? FileService.SAFE_DIR_NAME : FileService.DIR_NAME;
		model.addAttribute("rootDirName", rootDirName);

		// TODO csrfToken
		CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");
		log.debug("header = {}", csrfToken.getHeaderName());
		log.debug("param = {}", csrfToken.getParameterName());
		log.debug("token = {}", csrfToken.getToken());

		return "home";
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
