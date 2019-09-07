package com.haruu.filemanager.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.haruu.filemanager.service.FileService;

@Controller
@RequestMapping("/videos")
public class VideoController {

	@Autowired
	FileService fileService;

	@GetMapping(value = "/n/{name}")
	public String video(Model model, HttpServletRequest request,
			@PathVariable("name") String name) {

		model.addAttribute("videoName", name);

		String subName = name.substring(0, name.length() - 4);
		subName += ".vtt";

		model.addAttribute("subName", subName);

		model.addAttribute("videoDirName", FileService.DIR_NAME + FileService.VIDEOS);

		return "watch_video";
	}

	@GetMapping(value = "/s/{name}")
	public String safeVideo(Model model, HttpServletRequest request,
			@PathVariable("name") String name) {

		model.addAttribute("videoName", name);

		String subName = name.substring(0, name.length() - 4);
		subName += ".vtt";

		model.addAttribute("subName", subName);
		
		model.addAttribute("videoDirName", FileService.SAFE_DIR_NAME + FileService.VIDEOS);

		return "watch_video";
	}
}
