package com.ureca.uble.domain.store.dto.response;

import com.google.common.geometry.S2CellId;

import java.util.List;

public class StoreCacheResult {
    private final List<GetStoreRes> hitStores;
    private final List<S2CellId> missCells;

    public StoreCacheResult(List<GetStoreRes> hitStores, List<S2CellId> missedCells) {
        this.hitStores = hitStores;
        this.missCells = missedCells;
    }

    public List<GetStoreRes> getHitStores() {
        return hitStores;
    }

    public List<S2CellId> getMissCells() {
        return missCells;
    }
}
