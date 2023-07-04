package com.likelion.market.service;

import com.likelion.market.dto.NegotiationDto;
import com.likelion.market.dto.ResponseDto;
import com.likelion.market.entity.NegotiationEntity;
import com.likelion.market.repository.NegotiationRepository;
import com.likelion.market.repository.SalesItemRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class NegotiationService {
    private final SalesItemRepository salesItemRepository;
    private final NegotiationRepository negotiationRepository;

    // create suggest
    public ResponseDto create(Long itemId, NegotiationDto.CreateAndUpdateRequest requestDto) {
        if (!salesItemRepository.existsById(itemId)) throw new ResponseStatusException(HttpStatus.NOT_FOUND); // 아이템 존재하지 않음

        NegotiationEntity negotiation = new NegotiationEntity();
        negotiation.setItemId(itemId);
        negotiation.setSuggestedPrice(requestDto.getSuggestedPrice());
        negotiation.setStatus("제안");
        negotiation.setWriter(requestDto.getWriter());
        negotiation.setPassword(requestDto.getPassword());
        negotiationRepository.save(negotiation);

        ResponseDto response = new ResponseDto();
        response.setMessage("구매 제안이 등록되었습니다.");
        return response;
    }
}
