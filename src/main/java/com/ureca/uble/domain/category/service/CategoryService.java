package com.ureca.uble.domain.category.service;

import com.ureca.uble.domain.category.dto.response.GetCategoryDetailRes;
import com.ureca.uble.domain.category.dto.response.GetCategoryListRes;
import com.ureca.uble.domain.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    /**
     * 카테고리 전체 조회
     */
    public GetCategoryListRes getCategories() {
        return new GetCategoryListRes(categoryRepository.findAll().stream()
            .map(GetCategoryDetailRes::from)
            .toList());
    }
}
