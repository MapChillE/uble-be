package com.ureca.uble.domain.users.dto.request;

import java.time.LocalDate;
import java.util.List;

import com.ureca.uble.entity.enums.Gender;
import com.ureca.uble.entity.enums.Rank;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
@Schema(description = "사용자 정보 관련 요청 DTO")
public class UpdateUserInfoReq {

	@Schema(description = "요금제 등급", example = "VIP")
	private Rank rank;

	@Schema(description = "성별", example = "FEMALE")
	private Gender gender;

	@Schema(description = "생년월일", example = "2001.01.01")
	@NotNull(message = "생년월일은 필수입니다.")
	@Past(message = "생년월일은 과거 날짜여야 합니다.")
	private LocalDate birthDate;

	@Schema(description = "바코드 번호", example = "123456787654321")
	@NotBlank(message = "바코드 번호는 필수입니다.")
	@Size(min = 16, max = 16, message = "바코드는 16자로 입력해주세요.")
	private String barcode;

	@Schema(description = "카테고리 ID 리스트", example = "[1, 3, 5]")
	@NotEmpty(message = "관심 카테고리를 1개 이상 선택해주세요.")
	private List<@NotNull(message = "카테고리 ID는 null일 수 없습니다.") Long> categoryIds;
}
