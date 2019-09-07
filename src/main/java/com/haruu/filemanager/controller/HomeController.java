package com.haruu.filemanager.controller;

import java.io.File;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.haruu.filemanager.service.FileService;

import groovy.lang.Tuple2;

@Controller
public class HomeController {

	@Autowired
	private FileService fileService;

	@GetMapping(value = "/")
	public String home(Model model, HttpServletRequest request) {

		// 파일 목록
		File[] files = fileService.findAll(0);
		List<Tuple2<String, Long>> fileInfoList = fileService.getFileInfoList(files);
		model.addAttribute("fileInfoList", fileInfoList);

		// 영화 목록
		files = fileService.findAll(0, FileService.VIDEOS);
		List<Tuple2<String, Long>> videoInfoList = fileService.getVideoInfoList(files);

		model.addAttribute("videoInfoList", videoInfoList);

		model.addAttribute("dirName", FileService.DIR_NAME);

		return "home";
	}

	@GetMapping(value = "/safe")
	public String safeHome(Model model, HttpServletRequest request) {
		// 파일 목록
		File[] files = fileService.findAll(1);
		List<Tuple2<String, Long>> safeFileInfoList = fileService.getFileInfoList(files);
		model.addAttribute("safeFileInfoList", safeFileInfoList);

		// 영화 목록
		files = fileService.findAll(1, FileService.VIDEOS);
		List<Tuple2<String, Long>> safeVideoInfoList = fileService.getVideoInfoList(files);

		model.addAttribute("safeVideoInfoList", safeVideoInfoList);
		
		model.addAttribute("safeDirName", FileService.SAFE_DIR_NAME);

		return home(model, request);
	}

	@GetMapping(value = "/error")
	public String error() {
		return "error";
	}
}
