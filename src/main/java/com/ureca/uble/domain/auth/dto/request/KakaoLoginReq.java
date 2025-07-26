package com.ureca.uble.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
@Schema(description="카카오 로그인 요청")
public class KakaoLoginReq {


	@Schema(description = "카카오 인가 코드", example="abc123xyz")
	@NotBlank(message = "인가 코드는 필수입니다.")
	@Size(min = 10, max = 100, message = "인가 코드는 10자 이상 100자 이하로 입력해주세요.")
	private String code;

}
