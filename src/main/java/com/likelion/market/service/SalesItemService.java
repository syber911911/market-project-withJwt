package com.likelion.market.service;

import com.likelion.market.dto.PageDto;
import com.likelion.market.dto.SalesItemDto;
import com.likelion.market.entity.SalesItemEntity;
import com.likelion.market.repository.SalesItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
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

    // update item
    public SalesItemDto.Response updateItem(Long itemId, SalesItemDto.UpdateItemRequest requestDto) {
        Optional<SalesItemEntity> optionalItem = repository.findById(itemId);

        if (optionalItem.isPresent()) {
            SalesItemEntity item = optionalItem.get();
            if (item.getWriter().equals(requestDto.getWriter())) {
                if (item.getPassword().equals(requestDto.getPassword())) {
                    item.setTitle(requestDto.getTitle());
                    item.setDescription(requestDto.getDescription());
                    item.setMinPriceWanted(requestDto.getMinPriceWanted());
                    repository.save(item);

                    SalesItemDto.Response response = new SalesItemDto.Response();
                    response.setMessage("물품이 수정되었습니다.");
                    return response;
                } else throw new ResponseStatusException(HttpStatus.UNAUTHORIZED); // 비밀번호 오류
            } else throw new ResponseStatusException(HttpStatus.UNAUTHORIZED); // 작성자 오류
        } else throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    // update user
    public SalesItemDto.Response updateUser(Long itemId, SalesItemDto.UpdateUserRequest requestDto) {
        Optional<SalesItemEntity> optionalItem = repository.findById(itemId);

        if (optionalItem.isPresent()) {
            SalesItemEntity item = optionalItem.get();
            if (item.getWriter().equals(requestDto.getRecentUser().getWriter())) {
                if (item.getPassword().equals(requestDto.getRecentUser().getPassword())) {
                    item.setWriter(requestDto.getUpdateUser().getWriter());
                    item.setPassword(requestDto.getUpdateUser().getPassword());
                    repository.save(item);

                    SalesItemDto.Response response = new SalesItemDto.Response();
                    response.setMessage("작성자 정보가 수정되었습니다.");
                    return response;
                } else throw new ResponseStatusException(HttpStatus.UNAUTHORIZED); // 비밀번호 오류
            } else throw new ResponseStatusException(HttpStatus.UNAUTHORIZED); // 작성자 오류
        } else throw new ResponseStatusException(HttpStatus.BAD_REQUEST); // 아이템 존재하지 않음
    }

//    public SalesItemDto.Response updateUser(Long itemId, String writer, String password, SalesItemDto.User requestDto) {
//        Optional<SalesItemEntity> optionalItem = repository.findById(itemId);
//
//        if (optionalItem.isPresent()) {
//            SalesItemEntity item = optionalItem.get();
//            if (item.getWriter().equals(writer)) {
//                if (item.getPassword().equals(password)) {
//                    item.setWriter(requestDto.getWriter());
//                    item.setPassword(requestDto.getPassword());
//                    repository.save(item);
//
//                    SalesItemDto.Response response = new SalesItemDto.Response();
//                    response.setMessage("작성자 정보가 수정되었습니다.");
//                    return response;
//                } else throw new ResponseStatusException(HttpStatus.UNAUTHORIZED); //  비밀번호 오류
//            } else throw new ResponseStatusException(HttpStatus.UNAUTHORIZED); // 작성자 오류
//        } else throw new ResponseStatusException(HttpStatus.BAD_REQUEST); // 아이템 존재하지 않음
//    }

    // update image
    public SalesItemDto.Response updateItemImage(Long itemId, SalesItemDto.User requestDto, MultipartFile itemImage) {
        Optional<SalesItemEntity> optionalItem = repository.findById(itemId);

        if (optionalItem.isPresent()) {
            SalesItemEntity item = optionalItem.get();
            if (item.getWriter().equals(requestDto.getWriter())) {
                if (item.getPassword().equals(requestDto.getPassword())) {
                    String imageDir = "src/main/resources/images/";
                    try {
                        Files.createDirectories(Path.of(imageDir));
                    } catch (IOException e) {
                        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                    LocalDateTime createTime = LocalDateTime.now();

                    String originalFileName = itemImage.getOriginalFilename();
                    String[] fileNameSplit = originalFileName.split("\\.");
                    String extension = fileNameSplit[fileNameSplit.length - 1];
                    String itemImageFileName = String.format("%s_%s.%s", createTime.toString(), requestDto.getWriter(), extension);
                    String itemImagePath = imageDir + itemImageFileName;

                    try {
                        itemImage.transferTo(Path.of(itemImagePath));
                    } catch (IOException e) {
                        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
                    }

                    item.setImageUrl(String.format("/static/images/%s", itemImageFileName));
                    repository.save(item);

                    SalesItemDto.Response response = new SalesItemDto.Response();
                    response.setMessage("이미지가 등록되었습니다.");
                    return response;
                } else throw new ResponseStatusException(HttpStatus.UNAUTHORIZED); // 비밀번호 오류
            } else throw new ResponseStatusException(HttpStatus.UNAUTHORIZED); // 작성자 오류
        } else throw new ResponseStatusException(HttpStatus.BAD_REQUEST); // 아이템 존재하지 않음
    }
    // delete
    public SalesItemDto.Response deleteItem(Long itemId, SalesItemDto.User requestDto) {
        Optional<SalesItemEntity> optionalItem = repository.findById(itemId);

        if (optionalItem.isPresent()) {
            SalesItemEntity item = optionalItem.get();
            if (item.getWriter().equals(requestDto.getWriter())) {
                if (item.getPassword().equals(requestDto.getPassword())) {
                    repository.delete(item);

                    SalesItemDto.Response response = new SalesItemDto.Response();
                    response.setMessage("물품을 삭제했습니다.");
                    return response;
                } else throw new ResponseStatusException(HttpStatus.UNAUTHORIZED); // 비밀번호 오류
            } else throw new ResponseStatusException(HttpStatus.UNAUTHORIZED); // 작성자 오류
        } else throw new ResponseStatusException(HttpStatus.BAD_REQUEST); // 아이템 존재하지 않음
    }
}
