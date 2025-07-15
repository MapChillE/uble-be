package com.ureca.uble.global.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ureca.uble.global.response.CommonResponse;

@RestController
public class GlobalController {
	@GetMapping("/health")
	public CommonResponse<String> healthCheck() {
		return CommonResponse.success("OK");
	}
}
