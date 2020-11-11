package com.haruu.filemanager.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.haruu.filemanager.service.FileService;

@Controller
public class HomeController {
	private Logger log = LoggerFactory.getLogger(HomeController.class);

	@Autowired
	FileService fileService;
	
	@GetMapping("/")
	public String home(Model model, HttpServletRequest request) {
		model.addAttribute("rootDirName", FileService.DIR_NAME);
		if (request.isUserInRole("ADMIN"))
			model.addAttribute("rootSafeDirName", FileService.SAFE_DIR_NAME);
		else
			model.addAttribute("rootSafeDirName", null);
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
