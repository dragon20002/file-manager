package com.haruu.filemanager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

	@GetMapping(value = "/")
	public String home() {
		return "home";
	}

	@GetMapping(value = "/error")
	public String error() {
		return "error";
	}
}
