package com.ureca.uble.domain.users.dto.request;

import java.time.LocalDate;
import java.util.List;

import com.ureca.uble.entity.enums.Gender;
import com.ureca.uble.entity.enums.Rank;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "사용자 정보 관련 요청 DTO")
public class UpdateUserInfoReq {

	@Schema(description = "요금제 등급", example = "VIP")
	private Rank rank;

	@Schema(description = "성별", example = "FEMALE")
	private Gender gender;

	@Schema(description = "생년월일", example = "2001.01.01")
	private LocalDate birthDate;

	@Schema(description = "카테고리 ID 리스트", example = "[1, 3, 5]")
	private List<Long> categoryIds;
}
