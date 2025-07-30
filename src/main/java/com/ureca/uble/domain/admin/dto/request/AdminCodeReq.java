package com.ureca.uble.domain.admin.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
@Schema(description="Admin 검증 요청 DTO")
public class AdminCodeReq {
	@Schema(description = "Admin 검증 코드", example="abc123xyz")
	@NotBlank(message = "검증 코드는 필수입니다.")
	private String code;
}
