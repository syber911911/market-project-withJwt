package com.likelion.market.service;

import com.likelion.market.dto.PageDto;
import com.likelion.market.dto.SalesItemDto;
import com.likelion.market.entity.SalesItemEntity;
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
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SalesItemService {
    private final SalesItemRepository repository;

    // create
    public SalesItemDto.Response createItem(SalesItemDto.CreateRequest requestDto) {
        // 중복된 writer 입력에 대한 처리 필요
        if (repository.existsByWriter(requestDto.getWriter())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        SalesItemEntity newItem = new SalesItemEntity();
        newItem.setTitle(requestDto.getTitle());
        newItem.setDescription(requestDto.getDescription());
        newItem.setMinPriceWanted(requestDto.getMinPriceWanted());
        newItem.setWriter(requestDto.getWriter());
        newItem.setPassword(requestDto.getPassword());
        newItem.setStatus("판매중");
        repository.save(newItem);

        SalesItemDto.Response response = new SalesItemDto.Response();
        response.setMessage("등록이 완료되었습니다.");
        return response;
    }

    // 전체 조회
    public PageDto readItemPaged(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("id"));
        Page<SalesItemEntity> itemPage = repository.findAll(pageable);
        Page<SalesItemDto.ReadAllResponse> originItemDtoPage = itemPage.map(SalesItemDto.ReadAllResponse::fromEntity);
        return PageDto.makePage(originItemDtoPage);
    }

    // id 조회
    public SalesItemDto.ReadByIdResponse readItemById(Long itemId) {
        Optional<SalesItemEntity> optionalItem = repository.findById(itemId);

        if (optionalItem.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return SalesItemDto.ReadByIdResponse.fromEntity(optionalItem.get());
    }

    // update
    public SalesItemDto.Response updateItem(Long itemId, SalesItemDto.UpdateRequest requestDto) {
        Optional<SalesItemEntity> optionalItem = repository.findById(itemId);

        if (optionalItem.isPresent()) {
            SalesItemEntity item = optionalItem.get();
            if (item.getPassword().equals(requestDto.getPassword())) {
                item.setTitle(requestDto.getTitle());
                item.setDescription(requestDto.getDescription());
                item.setMinPriceWanted(requestDto.getMinPriceWanted());
                item.setWriter(requestDto.getWriter());
                repository.save(item);

                SalesItemDto.Response response = new SalesItemDto.Response();
                response.setMessage("물품이 수정되었습니다.");
                return response;
            } else throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        } else throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    // 이미지 첨부

    // delete
}
