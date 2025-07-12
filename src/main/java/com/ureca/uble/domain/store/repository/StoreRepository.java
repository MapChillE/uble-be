package com.ureca.uble.domain.store.repository;

import com.ureca.uble.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long>, CustomStoreRepository {

    @Query("SELECT s FROM Store s JOIN FETCH s.brand WHERE s.id = :storeId")
    Optional<Store> findByIdWithBrand(@Param("storeId") Long storeId);
}
