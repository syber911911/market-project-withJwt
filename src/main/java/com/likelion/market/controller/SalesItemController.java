package com.likelion.market.controller;

import com.likelion.market.dto.PageDto;
import com.likelion.market.dto.ResponseDto;
import com.likelion.market.dto.SalesItemDto;
import com.likelion.market.dto.UserDto;
import com.likelion.market.service.SalesItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class SalesItemController {
    private final SalesItemService service;

    @PostMapping
    public ResponseDto create(@RequestBody SalesItemDto.CreateAndUpdateRequest requestDto, @AuthenticationPrincipal String username) {
        log.info("username : {}", username);
        return service.createItem(requestDto, username);
    }

    @GetMapping
    public PageDto<SalesItemDto.ReadAllResponse> readAll(
            @RequestParam(value = "page", defaultValue = "1") Integer pageNumber,
            @RequestParam(value = "limit", defaultValue = "25") Integer pageSize
    ) {
        return service.readItemPaged(pageNumber > 0 ? pageNumber - 1 : 0, pageSize);
    }

    @GetMapping("/{itemId}")
    public SalesItemDto.ReadByIdResponse read(@PathVariable("itemId") Long itemId, @AuthenticationPrincipal String username) {
        log.info("username : {}", username);
        return service.readItemById(itemId);
    }

    @PutMapping(value = "/{itemId}/image", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseDto updateImage(
            @PathVariable("itemId") Long itemId,
//            @RequestParam("writer") String writer,
//            @RequestParam("password") String password,
//            @RequestPart("user") UserDto requestDto,
            @RequestPart("image") MultipartFile itemImage,
            @AuthenticationPrincipal String username
    ){
        log.info("username : {}, image : {}", username, itemImage);
        return service.updateItemImage(itemId, itemImage, username);
    }

    @PutMapping("/{itemId}")
    public ResponseDto updateItem(@PathVariable("itemId") Long itemId, @RequestBody SalesItemDto.CreateAndUpdateRequest requestDto, @AuthenticationPrincipal String username) {
        return service.updateItem(itemId, requestDto, username);
    }

//    @PutMapping("/{itemId}/user")
//    public ResponseDto updateUser(@PathVariable("itemId") Long itemId, @RequestBody UserDto.UpdateUserRequest requestDto){
//        return service.updateUser(itemId, requestDto);
//    }

//    @PutMapping(value = "/{itemId}", params = {"writer", "password"})
//    public ResponseDto updateUser(
//            @PathVariable("itemId") Long itemId,
//            @RequestParam(value = "writer", required = true) String writer,
//            @RequestParam(value = "password", required = true) String password,
//            @RequestBody SalesItemDto.UpdateUserRequest requestDto
//    ) {
//        return service.updateUser(itemId, writer, password, requestDto);
//    }

    @DeleteMapping("/{itemId}")
    public ResponseDto deleteItem(@PathVariable("itemId") Long itemId, @AuthenticationPrincipal String username) {
        return service.deleteItem(itemId, username);
    }
}


