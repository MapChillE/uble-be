package com.ureca.uble.domain.category.controller;

import com.ureca.uble.domain.category.dto.response.GetCategoryListRes;
import com.ureca.uble.domain.category.service.CategoryService;
import com.ureca.uble.domain.common.dto.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/category")
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * 카테고리 전체 조회
     */
    @Operation(summary = "카테고리 전체 조회", description = "카테고리 전체 조회")
    @GetMapping
    public CommonResponse<GetCategoryListRes> getCategories() {
        return CommonResponse.success(categoryService.getCategories());
    }
}
