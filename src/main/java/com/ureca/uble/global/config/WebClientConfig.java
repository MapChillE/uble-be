package com.ureca.uble.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

	@Value("${spring.elasticsearch.uris}")
	private String elasticUris;

	@Value("${fastapi.uris}")
	private String fastapiUris;

	@Bean
	public WebClient kakaoAuthWebClient(){
		return WebClient.builder()
			.baseUrl("https://kauth.kakao.com")
			.defaultHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
			.build();
	}

	@Bean
	public WebClient kakaoApiWebClient(){
		return WebClient.builder()
			.baseUrl("https://kapi.kakao.com")
			.defaultHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
			.build();
	}

	@Bean
	public WebClient elasticWebClient() {
		return WebClient.builder()
			.baseUrl(elasticUris)
			.defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
			.build();
	}

	@Bean
	public WebClient fastapiWebClient() {
		return WebClient.builder()
			.baseUrl(fastapiUris)
			.build();
	}
}
