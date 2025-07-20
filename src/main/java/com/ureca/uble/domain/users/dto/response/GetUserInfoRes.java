package com.ureca.uble.domain.users.dto.response;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.ureca.uble.entity.User;
import com.ureca.uble.entity.enums.Gender;
import com.ureca.uble.entity.enums.Rank;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description="사용자 정보 관련 응답 DTO")
public class GetUserInfoRes {
	@Schema(description = "사용자 닉네임", example = "김유블")
	private String nickname;

	@Schema(description = "요금제 등급", example = "VIP")
	private Rank rank;

	@Schema(description = "성별", example = "FEMALE")
	private Gender gender;

	@Schema(description = "생년월일", example = "2001.01.01")
	private LocalDate birthDate;

	@Schema(description = "바코드 번호", example = "123456787654321")
	private String barcode;

	@Schema(description = "카테고리 ID 리스트", example = "[1, 3, 5]")
	private List<Long> categoryIds;

	public static GetUserInfoRes of(User user, List<Long> categoryIds){
		List<Long> safeCategories = Objects.requireNonNullElse(categoryIds, Collections.emptyList());
		return GetUserInfoRes.builder()
			.nickname(user.getNickname())
			.rank(user.getRank())
			.gender(user.getGender())
			.birthDate(user.getBirthDate())
			.barcode(user.getBarcode())
			.categoryIds(safeCategories)
			.build();
	}

}
