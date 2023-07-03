package com.likelion.market.controller;

import com.likelion.market.dto.PageDto;
import com.likelion.market.dto.SalesItemDto;
import com.likelion.market.service.SalesItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class SalesItemController {
    private final SalesItemService service;

    @PostMapping
    public SalesItemDto.Response create(@RequestBody SalesItemDto.CreateRequest requestDto) {
        return service.createItem(requestDto);
    }

    @GetMapping
    public PageDto readAll(
            @RequestParam(value = "page", defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "limit", defaultValue = "20") Integer pageSize
    ) {
        return service.readItemPaged(pageNumber > 0 ? pageNumber - 1 : 0, pageSize);
    }

    @GetMapping("/{itemId}")
    public SalesItemDto.ReadByIdResponse read(@PathVariable("itemId") Long itemId) {
        return service.readItemById(itemId);
    }

    @PutMapping("/{itemId}")
    public SalesItemDto.Response updateItem(@PathVariable("itemId") Long itemId, @RequestBody SalesItemDto.UpdateItemRequest requestDto) {
        return service.updateItem(itemId, requestDto);
    }

    @PutMapping("/{itemId}/user")
    public SalesItemDto.Response updateUser(@PathVariable("itemId") Long itemId, @RequestBody SalesItemDto.UpdateUserRequest requestDto){
        return service.updateUser(itemId, requestDto);
    }

//    @PutMapping(value = "/{itemId}", params = {"writer", "password"})
//    public SalesItemDto.Response updateUser(
//            @PathVariable("itemId") Long itemId,
//            @RequestParam(value = "writer", required = true) String writer,
//            @RequestParam(value = "password", required = true) String password,
//            @RequestBody SalesItemDto.UpdateUserRequest requestDto
//    ) {
//        return service.updateUser(itemId, writer, password, requestDto);
//    }

    @DeleteMapping("/{itemId}")
    public SalesItemDto.Response deleteItem(@PathVariable("itemId") Long itemId, @RequestBody SalesItemDto.User requestDto) {
        return service.deleteItem(itemId, requestDto);
    }
}


