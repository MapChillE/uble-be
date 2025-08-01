package com.ureca.uble.domain.brand.fixture;

import com.ureca.uble.entity.Brand;
import com.ureca.uble.entity.Category;

public class BrandFixtures {
    public static Brand createTmpBrand(Category category) {
        return Brand.createTmpBrand(category);
    }
}
