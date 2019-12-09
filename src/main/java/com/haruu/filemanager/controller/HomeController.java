package com.haruu.filemanager.controller;

import java.io.File;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.haruu.filemanager.model.FileInfo;
import com.haruu.filemanager.service.FileService;

@Controller
public class HomeController {

	@Autowired
	private FileService fileService;

	@GetMapping(value = "/")
	public String home(Model model, HttpServletRequest request) {

		// 파일 목록
		File[] files = fileService.findAll(0);
		List<FileInfo> fileInfoList = fileService.getFileInfoList(files);
		model.addAttribute("fileInfoList", fileInfoList);

		// 영화 목록
		files = fileService.findAll(0, FileService.VIDEOS);
		List<FileInfo> videoInfoList = fileService.getVideoInfoList(files);

		model.addAttribute("videoInfoList", videoInfoList);

		model.addAttribute("dirName", FileService.DIR_NAME);

		if (request.isUserInRole("ADMIN")) {
			// 파일 목록
			files = fileService.findAll(1);
			List<FileInfo> safeFileInfoList = fileService.getFileInfoList(files);
			model.addAttribute("safeFileInfoList", safeFileInfoList);

			// 영화 목록
			files = fileService.findAll(1, FileService.VIDEOS);
			List<FileInfo> safeVideoInfoList = fileService.getVideoInfoList(files);

			model.addAttribute("safeVideoInfoList", safeVideoInfoList);

			model.addAttribute("safeDirName", FileService.SAFE_DIR_NAME);
		}

		return "home";
	}

	@GetMapping(value = "/login")
	public String login(Model model, HttpServletRequest request) {
		return "login";
	}

	@GetMapping(value = "/error")
	public String error() {
		return "error";
	}
}
