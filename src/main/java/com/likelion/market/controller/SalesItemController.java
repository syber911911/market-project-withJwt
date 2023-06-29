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

    @GetMapping("/{id}")
    public SalesItemDto.ReadByIdResponse read(@PathVariable("id") Long id) {
        return service.readItemById(id);
    }
}


