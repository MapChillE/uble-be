package com.ureca.uble.domain.common.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "커서 기반 페이지네이션 응답")
public class CursorPageRes<T> {
	@Schema(description = "데이터 리스트")
	private List<T> content;

	@Schema(description = "다음 페이지 존재 여부", example = "true")
	private boolean hasNext;

	@Schema(description = "마지막 커서 ID", example = "99")
	private Long lastCursorId;

	public static <T> CursorPageRes<T> of(List<T> content, boolean hasNext, Long lastCursorId) {
		return CursorPageRes.<T>builder()
			.content(content)
			.hasNext(hasNext)
			.lastCursorId(lastCursorId)
			.build();
	}
}
