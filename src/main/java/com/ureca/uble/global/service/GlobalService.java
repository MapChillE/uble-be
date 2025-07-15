package com.ureca.uble.global.service;

import com.ureca.uble.domain.brand.repository.*;
import com.ureca.uble.domain.store.repository.StoreNgramDocumentRepository;
import com.ureca.uble.domain.store.repository.StoreRepository;
import com.ureca.uble.entity.Brand;
import com.ureca.uble.entity.Store;
import com.ureca.uble.entity.document.BrandNgramDocument;
import com.ureca.uble.entity.document.BrandNoriDocument;
import com.ureca.uble.entity.document.CategoryNgramDocument;
import com.ureca.uble.entity.document.StoreNgramDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class GlobalService {

    private final StoreRepository storeRepository;
    private final StoreNgramDocumentRepository storeNgramDocumentRepository;
    private final BrandRepository brandRepository;
    private final BrandNoriDocumentRepository brandNoriDocumentRepository;
    private final BrandNgramDocumentRepository brandNgramDocumentRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryNgramDocumentRepository categoryNgramDocumentRepository;

    /**
     * ES 전체 정보 삽입 API
     */
    @Transactional
    public String updateIndex() {
        List<Brand> brandList = brandRepository.findAllWithCategoryAndBenefits();

        // Brand-nori 정보 전체 삽입
        List<BrandNoriDocument> noriBrands =brandList.stream()
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
}
