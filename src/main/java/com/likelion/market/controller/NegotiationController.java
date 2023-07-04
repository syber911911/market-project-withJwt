package com.likelion.market.controller;

import com.likelion.market.dto.NegotiationDto;
import com.likelion.market.dto.ResponseDto;
import com.likelion.market.service.NegotiationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/items/{itemId}/proposals")
@RestController
@RequiredArgsConstructor
public class NegotiationController {
    private final NegotiationService service;

    @PostMapping
    public ResponseDto create(@PathVariable("itemId") Long itemId, @RequestBody NegotiationDto.CreateAndUpdateRequest requestDto) {
        return service.create(itemId, requestDto);
    }
}
