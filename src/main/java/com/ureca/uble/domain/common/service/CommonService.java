package com.ureca.uble.domain.common.service;

import com.ureca.uble.domain.brand.repository.BrandNgramDocumentRepository;
import com.ureca.uble.domain.brand.repository.BrandNoriDocumentRepository;
import com.ureca.uble.domain.brand.repository.BrandRepository;
import com.ureca.uble.domain.category.repository.CategoryNgramDocumentRepository;
import com.ureca.uble.domain.category.repository.CategoryRepository;
import com.ureca.uble.domain.common.dto.request.CreateSearchLogReq;
import com.ureca.uble.domain.common.dto.response.CreateSearchLogRes;
import com.ureca.uble.domain.store.repository.SearchLogDocumentRepository;
import com.ureca.uble.domain.store.repository.StoreNgramDocumentRepository;
import com.ureca.uble.domain.store.repository.StoreRepository;
import com.ureca.uble.domain.users.repository.UserRepository;
import com.ureca.uble.entity.Brand;
import com.ureca.uble.entity.Store;
import com.ureca.uble.entity.User;
import com.ureca.uble.entity.document.*;
import com.ureca.uble.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.ureca.uble.domain.users.exception.UserErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CommonService {

    private final StoreRepository storeRepository;
    private final StoreNgramDocumentRepository storeNgramDocumentRepository;
    private final BrandRepository brandRepository;
    private final BrandNoriDocumentRepository brandNoriDocumentRepository;
    private final BrandNgramDocumentRepository brandNgramDocumentRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryNgramDocumentRepository categoryNgramDocumentRepository;
    private final SearchLogDocumentRepository searchLogDocumentRepository;
    private final UserRepository userRepository;

    /**
     * ES 전체 정보 삽입 API
     */
    @Transactional(readOnly = true)
    public String updateIndex() {
        List<Brand> brandList = brandRepository.findAllWithCategoryAndBenefits();

        // Brand-nori 정보 전체 삽입
        List<BrandNoriDocument> noriBrands = brandList.stream()
            .map(BrandNoriDocument::from)
            .toList();
        brandNoriDocumentRepository.saveAll(noriBrands);

        // Brand-ngram 정보 삽입
        List<BrandNgramDocument> ngramBrands = brandList.stream()
            .map(BrandNgramDocument::from)
            .toList();
        brandNgramDocumentRepository.saveAll(ngramBrands);

        // category 정보 삽입
        List<CategoryNgramDocument> categories = categoryRepository.findAll().stream()
            .map(CategoryNgramDocument::from)
            .toList();
        categoryNgramDocumentRepository.saveAll(categories);

        // store 정보 삽입
        try (Stream<Store> stream = storeRepository.findAllWithBrandAndCategory()) {
            List<StoreNgramDocument> stores = new ArrayList<>(500);
            stream.forEach(store -> {
                // online 아닌 애들 (위경도 있는 애들만, 지도에 표시할 수 있도록)
                if(!store.getBrand().getIsOnline() && store.getLocation() != null && !store.getLocation().isEmpty()) {
                    stores.add(StoreNgramDocument.from(store));
                }
                if (stores.size() == 500) {
                    storeNgramDocumentRepository.saveAll(stores);
                    stores.clear();
                }
            });
            if (!stores.isEmpty()) {
                storeNgramDocumentRepository.saveAll(stores);
            }
        }
        return "정보 삽입이 완료되었습니다.";
    }

    /**
     * 검색 로그 생성
     */
    public CreateSearchLogRes createSearchLog(Long userId, CreateSearchLogReq req) {
        User user = findUser(userId);
        SearchLogDocument savedDocument = searchLogDocumentRepository.save(SearchLogDocument.of(user, req.getSearchType(), req.getKeyword(), req.getIsResultExists()));
        return new CreateSearchLogRes(savedDocument.getId());
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new GlobalException(USER_NOT_FOUND));
    }
}
