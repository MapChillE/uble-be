package com.ureca.uble.domain.pin.controller;

import com.ureca.uble.domain.common.dto.response.CommonResponse;
import com.ureca.uble.domain.pin.dto.request.CreatePinReq;
import com.ureca.uble.domain.pin.dto.response.DeletePinRes;
import com.ureca.uble.domain.pin.dto.response.CreatePinRes;
import com.ureca.uble.domain.pin.dto.response.GetPinRes;
import com.ureca.uble.domain.pin.service.PinService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/pin")
@RequiredArgsConstructor
public class PinController {

    private final PinService pinService;

    @Operation(summary = "자주가는 곳 조회")
    @GetMapping
    public CommonResponse<GetPinRes> getInitialData(
            @Parameter(description = "사용자정보", required = true)
            @AuthenticationPrincipal Long userId) {
        return CommonResponse.success(pinService.getInitialData(userId));
    }

    @Operation(summary = "자주가는 곳 추가")
    @PostMapping
    public CommonResponse<CreatePinRes> createPin(
            @Parameter(description = "사용자 ID", required = true)
            @AuthenticationPrincipal Long userId,
            @Parameter(description = "자주 가는 곳 정보", required = true)
            @RequestBody CreatePinReq req) {
        CreatePinRes res = pinService.createPin(userId, req);
        return CommonResponse.success(res);
    }

    @Operation(summary = "자주가는 곳 삭제")
    @DeleteMapping("/{pinId}")
    public CommonResponse<DeletePinRes> deletePin(
            @Parameter(description = "사용자 ID", required = true)
            @AuthenticationPrincipal Long userId,
            @Parameter(description = "삭제할 pin ID", required = true)
            @PathVariable Long pinId) {
        pinService.deletePin(userId, pinId);
        return CommonResponse.success(null);
    }
}
