package com.likelion.market.service;

import com.likelion.market.dto.NegotiationDto;
import com.likelion.market.dto.PageDto;
import com.likelion.market.dto.ResponseDto;
import com.likelion.market.dto.UserDto;
import com.likelion.market.entity.NegotiationEntity;
import com.likelion.market.entity.SalesItemEntity;
import com.likelion.market.repository.NegotiationRepository;
import com.likelion.market.repository.SalesItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NegotiationService {
    private final SalesItemRepository salesItemRepository;
    private final NegotiationRepository negotiationRepository;

    // create suggest
    public ResponseDto createNegotiation(Long itemId, NegotiationDto.CreateAndUpdateRequest requestDto) {
        if (!salesItemRepository.existsById(itemId))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND); // 아이템 존재하지 않음

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

    // read
    public PageDto<NegotiationDto.ReadNegotiationResponse> readNegotiationPaged(
            Long itemId,
            String writer,
            String password,
            Integer pageNumber,
            Integer pageSize
    ) {
        if (!salesItemRepository.existsById(itemId))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND); // 아이템 존재하지 않음

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("id"));
        if (salesItemRepository.existsByIdAndWriterAndPassword(itemId, writer, password)) {
            // 요청한 사용자가 물품 등록자
            Page<NegotiationEntity> negotiationEntityPage = negotiationRepository.findAllByItemId(itemId, pageable);
            Page<NegotiationDto.ReadNegotiationResponse> originNegotiationDtoPage = negotiationEntityPage.map(NegotiationDto.ReadNegotiationResponse::fromEntity);
            PageDto<NegotiationDto.ReadNegotiationResponse> pageDto = new PageDto<>();
            return pageDto.makePage(originNegotiationDtoPage);
        }
        if (negotiationRepository.existsByItemIdAndWriterAndPassword(itemId, writer, password)) {
            // 요청한 사용자가 제안 등록자
            Page<NegotiationEntity> negotiationEntityPage = negotiationRepository.findAllByItemIdAndWriterAndPassword(itemId, writer, password, pageable);
            Page<NegotiationDto.ReadNegotiationResponse> originNegotiationDtoPage = negotiationEntityPage.map(NegotiationDto.ReadNegotiationResponse::fromEntity);
            PageDto<NegotiationDto.ReadNegotiationResponse> pageDto = new PageDto<>();
            return pageDto.makePage(originNegotiationDtoPage);
        } else throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        // 아이템과 제안이 모두 존재하지만
        // 요청한 사용자 정보가 물품 등록자, 제안 등록자 모두 아님
    }

    // update suggest
    public ResponseDto priceUpdate(Long itemId, Long id, NegotiationDto.CreateAndUpdateRequest requestDto) {
        if (negotiationRepository.existsByIdAndWriterAndPassword(id, requestDto.getWriter(), requestDto.getPassword())) {
            Optional<NegotiationEntity> optionalNegotiation = negotiationRepository.findByItemIdAndId(itemId, id);
            if (optionalNegotiation.isPresent()) {
                NegotiationEntity negotiation = optionalNegotiation.get();
                negotiation.setSuggestedPrice(requestDto.getSuggestedPrice());
                negotiationRepository.save(negotiation);

                ResponseDto response = new ResponseDto();
                response.setMessage("제안이 수정되었습니다.");
                return response;
            } else throw new ResponseStatusException(HttpStatus.NOT_FOUND); // 해당 아이템의 제안이 아니거나 해당하는 제안이 없음
        } else throw new ResponseStatusException(HttpStatus.UNAUTHORIZED); // 인증 오류
    }

    // update status
    public ResponseDto statusUpdate(Long itemId, Long id, NegotiationDto.CreateAndUpdateRequest requestDto, SalesItemEntity item) {
        if (requestDto.getStatus().equals("확정")) {
            // 제안 등록자의 요청
            if (negotiationRepository.existsByIdAndWriterAndPassword(id, requestDto.getWriter(), requestDto.getPassword())) {
                Optional<NegotiationEntity> optionalNegotiation = negotiationRepository.findByItemIdAndId(itemId, id);
                if (optionalNegotiation.isPresent()) {
                    NegotiationEntity negotiation = optionalNegotiation.get();
                    if (negotiation.getStatus().equals("수락")) {
                        negotiation.setStatus(requestDto.getStatus());
                        negotiationRepository.save(negotiation);

                        item.setStatus("판매 완료");
                        salesItemRepository.save(item);

                        // 아직 제안 상태인 제안 List
                        List<NegotiationEntity> remainderNegotiations = negotiationRepository.findAllByItemIdAndStatusIsLike(itemId, "제안");
                        if (!remainderNegotiations.isEmpty()) {
                            for (NegotiationEntity element : remainderNegotiations) {
                                element.setStatus("거절");
                            }
                            negotiationRepository.saveAll(remainderNegotiations);
                        }

                        ResponseDto response = new ResponseDto();
                        response.setMessage("구매가 확정되었습니다.");
                        return response;
                    } else throw new ResponseStatusException(HttpStatus.BAD_REQUEST); // 제안이 현재 수락 상태가 아님
                } else throw new ResponseStatusException(HttpStatus.NOT_FOUND); // 해당 아이템의 제안이 아니거나 해당하는 제안이 없음
            } else throw new ResponseStatusException(HttpStatus.UNAUTHORIZED); // 인증 오류
        }
        // 물품 등록자의 요청
        if (salesItemRepository.existsByIdAndWriterAndPassword(itemId, requestDto.getWriter(), requestDto.getPassword())) {
            Optional<NegotiationEntity> optionalNegotiation = negotiationRepository.findByItemIdAndId(itemId, id);
            if (optionalNegotiation.isPresent()) {
                NegotiationEntity negotiation = optionalNegotiation.get();
                if (negotiation.getStatus().equals("제안") && !negotiationRepository.existsByItemIdAndStatusLike(itemId, "수락")) {
                    negotiation.setStatus(requestDto.getStatus());
                    negotiationRepository.save(negotiation);

                    ResponseDto response = new ResponseDto();
                    response.setMessage("제안의 상태가 변경되었습니다.");
                    return response;
                } else throw new ResponseStatusException(HttpStatus.IM_USED); // 이미 해당 제안이 수락, 거절 또는 확정 상태 | 이미 수락 상태인 제안이 있음
            } else throw new ResponseStatusException(HttpStatus.NOT_FOUND); // 해당 아이템의 제안이 아니거나 해당하는 제안이 없음
        } else throw new ResponseStatusException(HttpStatus.UNAUTHORIZED); // 인증 오류
    }

    public ResponseDto negotiationUpdate(Long itemId, Long id, NegotiationDto.CreateAndUpdateRequest requestDto) {
        Optional<SalesItemEntity> optionalItem = salesItemRepository.findById(itemId);
        if (optionalItem.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND); // 아이템 존재하지 않음

        SalesItemEntity item = optionalItem.get();
        if (requestDto.getSuggestedPrice() != null) {
            // 가격 수정
            return priceUpdate(itemId, id, requestDto);
        }

        if (requestDto.getStatus() != null) {
            // 상태 수정
            return statusUpdate(itemId, id, requestDto, item);
        }

        throw new ResponseStatusException(HttpStatus.BAD_REQUEST); // 가격, 상태 수정 그 어떤 것도 아닌 경우
    }

    // delete suggest
    public ResponseDto negotiationDelete(Long itemId, Long proposalId, UserDto requestDto) {
        if (!salesItemRepository.existsById(itemId))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND); // 아이템 존재하지 않음

        Optional<NegotiationEntity> optionalNegotiation = negotiationRepository.findByItemIdAndId(itemId, proposalId);
        if (optionalNegotiation.isPresent()) {
            NegotiationEntity negotiation = optionalNegotiation.get();
            if (negotiation.getWriter().equals(requestDto.getWriter()) && negotiation.getPassword().equals(requestDto.getPassword())) {
                negotiationRepository.delete(negotiation);

                ResponseDto response = new ResponseDto();
                response.setMessage("제안을 삭제했습니다.");
                return response;
            } else throw new ResponseStatusException(HttpStatus.UNAUTHORIZED); // 인증 오류
        } else throw new ResponseStatusException(HttpStatus.NOT_FOUND); // 해당 아이템의 제안이 아니거나 해당하는 제안이 없음
    }

    // update user 추가
}
