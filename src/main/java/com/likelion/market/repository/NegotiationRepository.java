package com.likelion.market.repository;

import com.likelion.market.entity.NegotiationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NegotiationRepository extends JpaRepository<NegotiationEntity, Long> {
}
