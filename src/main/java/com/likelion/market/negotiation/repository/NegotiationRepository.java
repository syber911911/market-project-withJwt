package com.likelion.market.negotiation.repository;

import com.likelion.market.negotiation.entity.NegotiationEntity;
import com.likelion.market.salesItem.entity.SalesItemEntity;
import com.likelion.market.user.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NegotiationRepository extends JpaRepository<NegotiationEntity, Long> {
    Boolean existsBySalesItemAndStatusLike(SalesItemEntity salesItem, String status); // 해당 아이템의 제안 중 status 가 일치하는 제안이 있는지 판별
    List<NegotiationEntity> findAllBySalesItemAndStatusIsLike(SalesItemEntity salesItem, String status); // 해당 아이템의 제한 중 status 가 일치하는 제안들을 List 로 반환
    Page<NegotiationEntity> findAllBySalesItem(SalesItemEntity salesItem, Pageable pageable); // 해당 아이템의 제안 page 반환
    Page<NegotiationEntity> findAllBySalesItemAndUser(SalesItemEntity salesItem, UserEntity user, Pageable pageable); // 해당 아이템의 제안 중 요청한 작성자의 제한 page 반환
}
