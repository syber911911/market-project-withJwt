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
        // username 으로 userEntity 조회
        UserEntity user = jpaUserDetailsManager.getUser(username);

        SalesItemEntity newItem = new SalesItemEntity();
        newItem.setTitle(requestDto.getTitle());
        newItem.setDescription(requestDto.getDescription());
        newItem.setMinPriceWanted(requestDto.getMinPriceWanted());
        newItem.setStatus("판매중");
        newItem.setUser(user);
        salesItemRepository.save(newItem);

        ResponseDto response = new ResponseDto();
        response.setMessage("등록이 완료되었습니다.");
        return response;
    }

    // readAll
    public PageDto<SalesItemDto.ReadAllResponse> readItemPaged(Integer pageNumber, Integer pageSize) {
        // 조회할 때 추가할 pageable 객체 생성
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("id"));
        // pageable 객체의 정보를 바탕으로 등록된 전체 item 글 조회
        Page<SalesItemEntity> itemPage = salesItemRepository.findAll(pageable);
        // 조회한 전체 item 글 Dto 로 변환
        Page<SalesItemDto.ReadAllResponse> originItemDtoPage = itemPage.map(SalesItemDto.ReadAllResponse::fromEntity);
        // 변환된 Page 객체에서 필요한 정보만 추출해 새로운 pageDto 를 생성해 반환
        PageDto<SalesItemDto.ReadAllResponse> pageDto = new PageDto<>();
        return pageDto.makePage(originItemDtoPage);
    }

    // read
    public SalesItemDto.ReadByIdResponse readItemById(Long itemId) {
        // itemId 에 해당하는 글 조회
        SalesItemEntity salesItem = this.getSalesItem(itemId);
        // 조회한 entity 를 dto 로 변환 후 반환
        return SalesItemDto.ReadByIdResponse.fromEntity(salesItem);
    }

    // update item
    public ResponseDto updateItem(Long itemId, SalesItemDto.CreateAndUpdateRequest requestDto, String username) {
        // itemId 에 해당하는 글 조회
        SalesItemEntity salesItem = this.getSalesItem(itemId);

        // 조회한 item 글을 현재 요청을 보낸 사용자가 작성한 것인지 확인
        if (salesItem.getUser().getUsername().equals(username)) {
            // requestBody 정보를 추출해 조회한 item entity 에 적용 후 저장
            salesItem.setTitle(requestDto.getTitle());
            salesItem.setDescription(requestDto.getDescription());
            salesItem.setMinPriceWanted(requestDto.getMinPriceWanted());
            salesItemRepository.save(salesItem);

            ResponseDto response = new ResponseDto();
            response.setMessage("물품이 수정되었습니다.");
            return response;
        } else throw new UserException(UserExceptionType.WRONG_USER); // 해당 사용자가 작성한 글이 아님
    }

    // update image
    public ResponseDto updateItemImage(Long itemId, MultipartFile itemImage, String username) {
        // itemId 에 해당하는 글 조회
        SalesItemEntity salesItem = this.getSalesItem(itemId);

        // 조회한 item 글을 현재 요청을 보낸 사용자가 작성한 것인지 확인
        if (salesItem.getUser().getUsername().equals(username)) {
            // 등록된 image 가 저정될 디렉토리 경로
            String imageDir = "src/main/resources/images/";
            try {
                // 해당 경로 디렉토리 생성
                Files.createDirectories(Path.of(imageDir));
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            }
            LocalDateTime createTime = LocalDateTime.now();

            // 사용자가 첨부한 image filename 추출
            String originalFileName = itemImage.getOriginalFilename();
            // 확장자 추출
            String[] fileNameSplit = originalFileName.split("\\.");
            String extension = fileNameSplit[fileNameSplit.length - 1];
            // 저장할 image 의 filename 재설정 (생성시간_username.확장자)
            String itemImageFileName = String.format("%s_%s.%s", createTime.toString(), username, extension);
            // image 저장 경로와 filename 을 합쳐 최종적으로 저장될 path 생성
            String itemImagePath = imageDir + itemImageFileName;

            try {
                // 생성한 path 에 image 저장
                itemImage.transferTo(Path.of(itemImagePath));
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            }

            // itemEntity 에 image 경로 추가 및 저장
            salesItem.setImageUrl(String.format("/static/images/%s", itemImageFileName));
            salesItemRepository.save(salesItem);

            ResponseDto response = new ResponseDto();
            response.setMessage("이미지가 등록되었습니다.");
            return response;
        } else throw new UserException(UserExceptionType.WRONG_USER); // 해당 user 가 작성한 item 이 아님
    }

    // delete
    public ResponseDto deleteItem(Long itemId, String username) {
        // itemId 에 해당하는 글 조회
        SalesItemEntity salesItem = this.getSalesItem(itemId);

        // 조회한 item 글을 현재 요청을 보낸 사용자가 작성한 것인지 확인
        if (salesItem.getUser().getUsername().equals(username)) {
            // item 삭제
            salesItemRepository.delete(salesItem);

            ResponseDto response = new ResponseDto();
            response.setMessage("물품을 삭제했습니다.");
            return response;
        } else throw new UserException(UserExceptionType.WRONG_USER); // 해당 user 가 작성한 item 이 아님
    }

    // itemId 로 DB 조회 및 결과 반환
    public SalesItemEntity getSalesItem(Long itemId) {
        Optional<SalesItemEntity> optionalItem = salesItemRepository.findById(itemId);
        if (optionalItem.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND); // 아이템이 존재하지 않음
        return optionalItem.get();
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
}
