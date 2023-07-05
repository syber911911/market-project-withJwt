package com.likelion.market.repository;

import com.likelion.market.entity.SalesItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalesItemRepository extends JpaRepository<SalesItemEntity, Long> {
    Boolean existsByIdAndWriterAndPassword(Long id, String writer, String password);
}
