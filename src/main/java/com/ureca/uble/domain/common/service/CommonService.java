package com.ureca.uble.domain.common.service;

import com.ureca.uble.domain.brand.repository.BrandNoriDocumentRepository;
import com.ureca.uble.domain.brand.repository.BrandRepository;
import com.ureca.uble.domain.brand.repository.BrandSuggestionDocumentRepository;
import com.ureca.uble.domain.category.repository.CategoryRepository;
import com.ureca.uble.domain.category.repository.CategorySuggestionDocumentRepository;
import com.ureca.uble.domain.store.repository.StoreRepository;
import com.ureca.uble.domain.store.repository.StoreSuggestionDocumentRepository;
import com.ureca.uble.entity.Brand;
import com.ureca.uble.entity.Store;
import com.ureca.uble.entity.document.BrandNoriDocument;
import com.ureca.uble.entity.document.BrandSuggestionDocument;
import com.ureca.uble.entity.document.CategorySuggestionDocument;
import com.ureca.uble.entity.document.StoreSuggestionDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CommonService {

    private final StoreRepository storeRepository;
    private final StoreSuggestionDocumentRepository storeSuggestionDocumentRepository;
    private final BrandRepository brandRepository;
    private final BrandNoriDocumentRepository brandNoriDocumentRepository;
    private final BrandSuggestionDocumentRepository brandSuggestionDocumentRepository;
    private final CategoryRepository categoryRepository;
    private final CategorySuggestionDocumentRepository categorySuggestionDocumentRepository;

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

        // Brand-suggestion 정보 삽입
        List<BrandSuggestionDocument> ngramBrands = brandList.stream()
            .map(BrandSuggestionDocument::from)
            .toList();
        brandSuggestionDocumentRepository.saveAll(ngramBrands);

        // category 정보 삽입
        List<CategorySuggestionDocument> categories = categoryRepository.findAll().stream()
            .map(CategorySuggestionDocument::from)
            .toList();
        categorySuggestionDocumentRepository.saveAll(categories);

        // store 정보 삽입
        try (Stream<Store> stream = storeRepository.findAllWithBrandAndCategory()) {
            List<StoreSuggestionDocument> stores = new ArrayList<>(500);
            stream.forEach(store -> {
                // online 아닌 애들 (위경도 있는 애들만, 지도에 표시할 수 있도록)
                if(!store.getBrand().getIsOnline() && store.getLocation() != null && !store.getLocation().isEmpty()) {
                    stores.add(StoreSuggestionDocument.from(store));
                }
                if (stores.size() == 500) {
                    storeSuggestionDocumentRepository.saveAll(stores);
                    stores.clear();
                }
            });
            if (!stores.isEmpty()) {
                storeSuggestionDocumentRepository.saveAll(stores);
            }
        }
        return "정보 삽입이 완료되었습니다.";
    }
}
