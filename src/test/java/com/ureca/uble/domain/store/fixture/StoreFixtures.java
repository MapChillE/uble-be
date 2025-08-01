package com.ureca.uble.domain.store.fixture;

import com.ureca.uble.entity.Brand;
import com.ureca.uble.entity.Store;

public class StoreFixtures {
    public static Store createTmpStore(Brand brand) {
        return Store.createTmpStore(brand);
    }
}
