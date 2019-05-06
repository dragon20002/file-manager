package com.haruu.filemanager.controller;

import java.io.File;
import java.util.ArrayList;
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
		File[] files = fileService.findAll();
		
		List<Tuple2<String, Long>> fileInfoList = new ArrayList<>();
		for (File file : files) {
			Tuple2<String, Long> fileInfo = new Tuple2<>(file.getName(), file.length() / 1024);
			fileInfoList.add(fileInfo);
		}
		
		model.addAttribute("fileInfoList", fileInfoList);

		String filePath = request.getLocalAddr() + ':' + request.getLocalPort() + '/' + FileService.DIR_NAME;
		model.addAttribute("filePath", filePath);

		return "home";
	}

	@GetMapping(value = "/error")
	public String error() {
		return "error";
	}
}
