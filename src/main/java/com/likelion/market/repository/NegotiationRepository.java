package com.likelion.market.repository;

import com.likelion.market.entity.NegotiationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NegotiationRepository extends JpaRepository<NegotiationEntity, Long> {
    boolean existsByIdAndWriterAndPassword(Long id, String writer, String password);
    boolean existsByItemIdAndWriterAndPassword(Long itemId, String writer, String password);
    boolean existsByItemIdAndStatusLike(Long itemId, String status);

    List<NegotiationEntity> findAllByItemIdAndStatusIsLike(Long itemId, String status);
    Optional<NegotiationEntity> findByItemIdAndId(Long itemId, Long id);
    Page<NegotiationEntity> findAllByItemId(Long itemId, Pageable pageable);
    Page<NegotiationEntity> findAllByItemIdAndWriterAndPassword(Long itemId, String writer, String password, Pageable pageable);
}
