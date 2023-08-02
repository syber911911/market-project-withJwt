package com.likelion.market.salesItem.service;

import com.likelion.market.global.dto.PageDto;
import com.likelion.market.global.dto.ResponseDto;
import com.likelion.market.salesItem.dto.SalesItemDto;
import com.likelion.market.salesItem.entity.SalesItemEntity;
import com.likelion.market.user.entity.UserEntity;
import com.likelion.market.user.exception.UserException;
import com.likelion.market.user.exception.UserExceptionType;
import com.likelion.market.salesItem.repository.SalesItemRepository;
import com.likelion.market.user.service.JpaUserDetailsManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final SalesItemRepository salesItemRepository;
    private final JpaUserDetailsManager jpaUserDetailsManager;

    // create
    public ResponseDto createItem(SalesItemDto.CreateAndUpdateRequest requestDto, String username) {
        UserEntity user = jpaUserDetailsManager.getUser(username);

        SalesItemEntity newItem = new SalesItemEntity();
        newItem.setTitle(requestDto.getTitle());
        newItem.setDescription(requestDto.getDescription());
        newItem.setMinPriceWanted(requestDto.getMinPriceWanted());
        newItem.setWriter(requestDto.getWriter());
        newItem.setPassword(requestDto.getPassword());
        newItem.setStatus("판매중");
        newItem.setUser(user);
        salesItemRepository.save(newItem);

        ResponseDto response = new ResponseDto();
        response.setMessage("등록이 완료되었습니다.");
        return response;
    }

    // 전체 조회
    public PageDto<SalesItemDto.ReadAllResponse> readItemPaged(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("id"));
        Page<SalesItemEntity> itemPage = salesItemRepository.findAll(pageable);
        Page<SalesItemDto.ReadAllResponse> originItemDtoPage = itemPage.map(SalesItemDto.ReadAllResponse::fromEntity);
        PageDto<SalesItemDto.ReadAllResponse> pageDto = new PageDto<>();
        return pageDto.makePage(originItemDtoPage);
    }

    // id 조회
    public SalesItemDto.ReadByIdResponse readItemById(Long itemId) {
        Optional<SalesItemEntity> optionalItem = salesItemRepository.findById(itemId);

        if (optionalItem.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return SalesItemDto.ReadByIdResponse.fromEntity(optionalItem.get());
    }

    // update item
    public ResponseDto updateItem(Long itemId, SalesItemDto.CreateAndUpdateRequest requestDto, String username) {
        Optional<SalesItemEntity> optionalItem = salesItemRepository.findById(itemId);
        if (optionalItem.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        SalesItemEntity item = optionalItem.get();

        if (item.getUser().getUsername().equals(username)) {
            item.setTitle(requestDto.getTitle());
            item.setDescription(requestDto.getDescription());
            item.setMinPriceWanted(requestDto.getMinPriceWanted());
            salesItemRepository.save(item);

            ResponseDto response = new ResponseDto();
            response.setMessage("물품이 수정되었습니다.");
            return response;
        } else throw new UserException(UserExceptionType.WRONG_USER); // 해당 user 가 작성한 item 이 아님
    }

//    // update user
//    public ResponseDto updateUser(Long itemId, UserDto.UpdateUserRequest requestDto) {
//        Optional<SalesItemEntity> optionalItem = salesItemRepository.findById(itemId);
//
//        if (optionalItem.isPresent()) {
//            SalesItemEntity item = optionalItem.get();
//            if (item.getWriter().equals(requestDto.getRecentUser().getWriter()) && item.getPassword().equals(requestDto.getRecentUser().getPassword())) {
//                item.setWriter(requestDto.getUpdateUser().getWriter());
//                item.setPassword(requestDto.getUpdateUser().getPassword());
//                salesItemRepository.save(item);
//
//                ResponseDto response = new ResponseDto();
//                response.setMessage("작성자 정보가 수정되었습니다.");
//                return response;
//            } else throw new ResponseStatusException(HttpStatus.UNAUTHORIZED); // 인증 오류
//        } else throw new ResponseStatusException(HttpStatus.NOT_FOUND); // 아이템 존재하지 않음
//    }

//    public SalesItemDto.Response updateUser(Long itemId, String writer, String password, SalesItemDto.User requestDto) {
//        Optional<SalesItemEntity> optionalItem = salesItemRepository.findById(itemId);
//
//        if (optionalItem.isPresent()) {
//            SalesItemEntity item = optionalItem.get();
//            if (item.getWriter().equals(writer)) {
//                if (item.getPassword().equals(password)) {
//                    item.setWriter(requestDto.getWriter());
//                    item.setPassword(requestDto.getPassword());
//                    salesItemRepository.save(item);
//
//                    SalesItemDto.Response response = new SalesItemDto.Response();
//                    response.setMessage("작성자 정보가 수정되었습니다.");
//                    return response;
//                } else throw new ResponseStatusException(HttpStatus.UNAUTHORIZED); //  비밀번호 오류
//            } else throw new ResponseStatusException(HttpStatus.UNAUTHORIZED); // 작성자 오류
//        } else throw new ResponseStatusException(HttpStatus.NOT_FOUND); // 아이템 존재하지 않음
//    }

    // update image
    public ResponseDto updateItemImage(Long itemId, MultipartFile itemImage, String username) {
        Optional<SalesItemEntity> optionalItem = salesItemRepository.findById(itemId);
        if (optionalItem.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        SalesItemEntity item = optionalItem.get();

        if (item.getUser().getUsername().equals(username)) {
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
            String itemImageFileName = String.format("%s_%s.%s", createTime.toString(), username, extension);
            String itemImagePath = imageDir + itemImageFileName;

            try {
                itemImage.transferTo(Path.of(itemImagePath));
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            }

            item.setImageUrl(String.format("/static/images/%s", itemImageFileName));
            salesItemRepository.save(item);

            ResponseDto response = new ResponseDto();
            response.setMessage("이미지가 등록되었습니다.");
            return response;
        } else throw new UserException(UserExceptionType.WRONG_USER); // 해당 user 가 작성한 item 이 아님
    }

    // delete
    public ResponseDto deleteItem(Long itemId, String username) {
        Optional<SalesItemEntity> optionalItem = salesItemRepository.findById(itemId);
        if (optionalItem.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND); // 아이템이 존재하지 않음

        SalesItemEntity item = optionalItem.get();
        if (item.getUser().getUsername().equals(username)) {
            salesItemRepository.delete(item);

            ResponseDto response = new ResponseDto();
            response.setMessage("물품을 삭제했습니다.");
            return response;
        } else throw new UserException(UserExceptionType.WRONG_USER); // 해당 user 가 작성한 item 이 아님

    }
}
