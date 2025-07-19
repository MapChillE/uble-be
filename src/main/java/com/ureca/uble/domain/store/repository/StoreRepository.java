package com.ureca.uble.domain.store.repository;

import com.ureca.uble.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.stream.Stream;

public interface StoreRepository extends JpaRepository<Store, Long>, CustomStoreRepository {

    @Query("SELECT s FROM Store s " +
        "JOIN FETCH s.brand b " +
        "JOIN FETCH b.category " +
        "WHERE s.id = :storeId")
    Optional<Store> findByIdWithBrandAndCategory(@Param("storeId") Long storeId);

    @Query("SELECT s FROM Store s " +
        "JOIN FETCH s.brand b " +
        "JOIN FETCH b.category " +
        "LEFT JOIN FETCH b.benefits " +
        "WHERE s.id = :storeId")
    Optional<Store> findByIdWithBrandAndCategoryAndBenefits(@Param("storeId") Long storeId);

    @Query("""
    SELECT s FROM Store s
    JOIN FETCH s.brand b
    JOIN FETCH b.category
    """)
    Stream<Store> findAllWithBrandAndCategory();

}
