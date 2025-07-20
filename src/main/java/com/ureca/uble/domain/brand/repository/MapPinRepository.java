package com.ureca.uble.domain.brand.repository;

import com.ureca.uble.entity.Pin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MapPinRepository extends JpaRepository<Pin, Long> {
    List<Pin> findByUserIdOrderByIdAsc(Long userId);
}
