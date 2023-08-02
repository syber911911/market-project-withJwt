package com.likelion.market.service;

import com.likelion.market.dto.NegotiationDto;
import com.likelion.market.dto.PageDto;
import com.likelion.market.dto.ResponseDto;
import com.likelion.market.dto.UserDto;
import com.likelion.market.entity.NegotiationEntity;
import com.likelion.market.entity.SalesItemEntity;
import com.likelion.market.entity.UserEntity;
import com.likelion.market.exception.UserException;
import com.likelion.market.exception.UserExceptionType;
import com.likelion.market.repository.NegotiationRepository;
import com.likelion.market.repository.SalesItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NegotiationService {
    private final SalesItemRepository salesItemRepository;
    private final NegotiationRepository negotiationRepository;
    private final JpaUserDetailsManager jpaUserDetailsManager;

    // create suggest
    public ResponseDto createNegotiation(Long itemId, NegotiationDto.CreateAndUpdateRequest requestDto, String username) {
        Optional<SalesItemEntity> optionalSalesItem = salesItemRepository.findById(itemId);
        if (optionalSalesItem.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND); // 아이템이 존재하지 않음
        SalesItemEntity salesItem = optionalSalesItem.get();

        UserEntity user = jpaUserDetailsManager.getUser(username);

        if (!salesItem.getUser().equals(user)) {
            NegotiationEntity negotiation = new NegotiationEntity();
            negotiation.setSuggestedPrice(requestDto.getSuggestedPrice());
            negotiation.setStatus("제안");
            negotiation.setUser(user);
            negotiation.setSalesItem(salesItem);
//        negotiation.setWriter(requestDto.getWriter());
//        negotiation.setPassword(requestDto.getPassword());
            negotiationRepository.save(negotiation);

            ResponseDto response = new ResponseDto();
            response.setMessage("구매 제안이 등록되었습니다.");
            return response;
        } else throw new ResponseStatusException(HttpStatus.BAD_REQUEST); // 아이템 작성자가 제안을 생성
    }

    // read
    public PageDto<NegotiationDto.ReadNegotiationResponse> readNegotiationPaged(
            Long itemId,
//            String writer,
//            String password,
            Integer pageNumber,
            Integer pageSize,
            String username
    ) {
        Optional<SalesItemEntity> optionalSalesItem = salesItemRepository.findById(itemId);
        if (optionalSalesItem.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        SalesItemEntity salesItem = optionalSalesItem.get();

        UserEntity user = jpaUserDetailsManager.getUser(username);
        log.info(user.getUsername());

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("id"));
        if (salesItem.getUser().equals(user)) {
            // 요청한 사용자가 물품 등록자
            // 해당 아이템에 대한 모든 제안 read
            Page<NegotiationEntity> negotiationEntityPage = negotiationRepository.findAllBySalesItem(salesItem, pageable);
            Page<NegotiationDto.ReadNegotiationResponse> originNegotiationDtoPage = negotiationEntityPage.map(NegotiationDto.ReadNegotiationResponse::fromEntity);
            PageDto<NegotiationDto.ReadNegotiationResponse> pageDto = new PageDto<>();
            return pageDto.makePage(originNegotiationDtoPage);
        }
        Page<NegotiationEntity> negotiationEntityPage = negotiationRepository.findAllBySalesItemAndUser(salesItem, user, pageable);
        Page<NegotiationDto.ReadNegotiationResponse> originNegotiationDtoPage = negotiationEntityPage.map(NegotiationDto.ReadNegotiationResponse::fromEntity);
        PageDto<NegotiationDto.ReadNegotiationResponse> pageDto = new PageDto<>();
        return pageDto.makePage(originNegotiationDtoPage);
    }

    // update price
    public ResponseDto priceUpdate(Integer suggestPrice, NegotiationEntity negotiation, SalesItemEntity salesItem, UserEntity user) {
        if (negotiation.getSalesItem().equals(salesItem)) {
            if (negotiation.getUser().equals(user)) {
                negotiation.setSuggestedPrice(suggestPrice);
                negotiationRepository.save(negotiation);

                ResponseDto response = new ResponseDto();
                response.setMessage("제안이 수정되었습니다.");
                return response;
            } else throw new UserException(UserExceptionType.WRONG_USER); // 해당 제안 작성자가 아님
        } else throw new ResponseStatusException(HttpStatus.BAD_REQUEST); // 해당 아이템의 제안이 아님
    }

    // update status
    public ResponseDto statusUpdate(String status, NegotiationEntity negotiation, SalesItemEntity salesItem, UserEntity user) {
        if (negotiation.getSalesItem().equals(salesItem)) {
            // 해당 요청은 물품 등록자 요청을 처리
            // 제안 작성자의 확정 요청이 아닌 경우
            if (!status.equals("확정")) {
                if (salesItem.getUser().equals(user)) {
                    if (negotiation.getStatus().equals("제안") && !negotiationRepository.existsBySalesItemAndStatusLike(salesItem, "수락")) {
                        negotiation.setStatus(status);
                        negotiationRepository.save(negotiation);

                        ResponseDto response = new ResponseDto();
                        response.setMessage("제안의 상태가 변경되었습니다.");
                        return response;
                    } else throw new ResponseStatusException(HttpStatus.IM_USED); // 이미 해당 제안이 수락, 거절 또는 확정 상태 || 이미 수락 상태인 제안이 존재함
                } else throw new UserException(UserExceptionType.WRONG_USER); // 아이템 작성자가 아님
            }
            // 해당 요청은 제안 등록자 요청을 처리
            if (negotiation.getUser().equals(user)) {
                // 제안이 수락 상태인 경우
                if (negotiation.getStatus().equals("수락")) {
                    negotiation.setStatus(status);
                    negotiationRepository.save(negotiation);

                    // 아이템의 상태 update
                    salesItem.setStatus("판매 완료");
                    salesItemRepository.save(salesItem);

                    // 아직 제안 상태인 제안 List
                    // 거절 상태 update
                    List<NegotiationEntity> remainderNegotiations = negotiationRepository.findAllBySalesItemAndStatusIsLike(salesItem, "제안");
                    // 제안 상태인 제안이 존재하는 경우
                    if (!remainderNegotiations.isEmpty()) {
                        for (NegotiationEntity element : remainderNegotiations) {
                            element.setStatus("거절");
                        }
                        negotiationRepository.saveAll(remainderNegotiations);
                    }

                    ResponseDto response = new ResponseDto();
                    response.setMessage("구매가 확정되었습니다.");
                    return response;
                } else throw new ResponseStatusException(HttpStatus.BAD_REQUEST); // 해당 제안이 현재 수락 상태가 아님
            } else throw new UserException(UserExceptionType.WRONG_USER); // 제안 작성자가 아님
        } else throw new ResponseStatusException(HttpStatus.BAD_REQUEST); // 해당 물품의 제안이 아님
    }

    public ResponseDto negotiationUpdate(Long itemId, Long id, NegotiationDto.CreateAndUpdateRequest requestDto, String username) {
        Optional<SalesItemEntity> optionalSalesItem = salesItemRepository.findById(itemId);
        if (optionalSalesItem.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND); // 아이템 존재하지 않음
        SalesItemEntity salesItem = optionalSalesItem.get();

        Optional<NegotiationEntity> optionalNegotiation = negotiationRepository.findById(id);
        if (optionalNegotiation.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND); // 제안이 존재하지 않음
        NegotiationEntity negotiation = optionalNegotiation.get();

        UserEntity user = jpaUserDetailsManager.getUser(username);

        // 한 사용자가 가격의 수정과 제안의 수정 두 가지 모두를 요청하는 경우
        if (requestDto.getStatus() != null && requestDto.getSuggestedPrice() != null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        if (requestDto.getSuggestedPrice() != null) {
            // 가격 수정
            return priceUpdate(requestDto.getSuggestedPrice(), negotiation, salesItem, user);
        }

        if (requestDto.getStatus() != null) {
            // 상태 수정
            return statusUpdate(requestDto.getStatus(), negotiation, salesItem, user);
        }

        throw new ResponseStatusException(HttpStatus.BAD_REQUEST); // 가격, 상태 수정 그 어떤 것도 아닌 경우
    }

    // delete suggest
    public ResponseDto negotiationDelete(Long itemId, Long id, UserDto requestDto, String username) {
        Optional<SalesItemEntity> optionalSalesItem = salesItemRepository.findById(itemId);
        if (optionalSalesItem.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND); // 아이템이 존재하지 않음
        SalesItemEntity salesItem = optionalSalesItem.get();

        Optional<NegotiationEntity> optionalNegotiation = negotiationRepository.findById(id);
        if (optionalNegotiation.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND); // 제안이 존재하지 않음
        NegotiationEntity negotiation = optionalNegotiation.get();

        UserEntity user = jpaUserDetailsManager.getUser(username);

        if (negotiation.getSalesItem().equals(salesItem)) {
            if (negotiation.getUser().equals(user)) {
                negotiationRepository.delete(negotiation);

                ResponseDto response = new ResponseDto();
                response.setMessage("제안을 삭제했습니다.");
                return response;
            } else throw new UserException(UserExceptionType.WRONG_USER); // 해당 제안의 작성자가 아님
        } else throw new ResponseStatusException(HttpStatus.BAD_REQUEST); // 해당 아이템의 제안이 아님
    }
}
