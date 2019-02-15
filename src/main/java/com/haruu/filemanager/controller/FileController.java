package com.haruu.filemanager.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.haruu.filemanager.service.FileService;

@Controller
@RequestMapping("/files")
public class FileController {

	@Autowired
	private FileService service;

	@PostMapping(value = "")
	public String addFiles(@RequestParam("files") MultipartFile[] files, Model model) {

		try {
			service.saveAll(files);
			model.addAttribute("success");
		} catch (IllegalStateException | IOException e) {
			e.printStackTrace();
			model.addAttribute("error");
		}

		return "redirect:/";
	}

}
