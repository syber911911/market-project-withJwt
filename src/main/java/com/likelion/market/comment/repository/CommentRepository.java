package com.likelion.market.comment.repository;

import com.likelion.market.comment.entity.CommentEntity;
import com.likelion.market.salesItem.entity.SalesItemEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    Page<CommentEntity> findAllBySalesItem(SalesItemEntity salesItem, Pageable pageable);
}
