package com.haruu.filemanager.controller;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.haruu.filemanager.service.FileService;
import com.haruu.filemanager.util.Common;

@Controller
@RequestMapping("/videos")
public class VideoController {

	@Autowired
	FileService fileService;

	@GetMapping(value = "/n")
	public String video(Model model, HttpServletRequest request,
			@RequestParam String name) throws UnsupportedEncodingException {

		System.out.println(name);
		model.addAttribute("videoName", name);
		model.addAttribute("encVideoName", Common.rawUrlEncode(name));

		String subName = name.substring(0, name.length() - 4);
		fileService.convertSubtitle(0, subName);
		subName += ".vtt";

		model.addAttribute("subName", Common.rawUrlEncode(subName));

		model.addAttribute("videoDirName", FileService.DIR_NAME + FileService.VIDEOS);

		return "watch_video";
	}

	@GetMapping(value = "/s")
	public String safeVideo(Model model, HttpServletRequest request,
			@RequestParam String name) throws UnsupportedEncodingException {
		
		model.addAttribute("videoName", name);
		model.addAttribute("encVideoName", Common.rawUrlEncode(name));

		String subName = name.substring(0, name.length() - 4);
		fileService.convertSubtitle(1, subName);
		subName += ".vtt";

		model.addAttribute("subName", Common.rawUrlEncode(subName));

		model.addAttribute("videoDirName", FileService.SAFE_DIR_NAME + FileService.VIDEOS);

		return "watch_video";
	}
}
