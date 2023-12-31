package com.likelion.market.negotiation.controller;

import com.likelion.market.negotiation.dto.NegotiationDto;
import com.likelion.market.global.dto.PageDto;
import com.likelion.market.global.dto.ResponseDto;
import com.likelion.market.user.dto.UserDto;
import com.likelion.market.negotiation.service.NegotiationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("/items/{itemId}/proposals")
@RestController
@RequiredArgsConstructor
public class NegotiationController {
    private final NegotiationService service;

    @PostMapping
    public ResponseDto create(@PathVariable("itemId") Long itemId, @RequestBody NegotiationDto.CreateAndUpdateRequest requestDto, @AuthenticationPrincipal String username) {
        log.info(username);
        return service.createNegotiation(itemId, requestDto, username);
    }

    @GetMapping
    public PageDto<NegotiationDto.ReadNegotiationResponse> readAll(
            @PathVariable("itemId") Long itemId,
            @RequestParam(value = "page", defaultValue = "1") Integer pageNumber,
            @RequestParam(value = "limit", defaultValue = "25") Integer pageSize,
            @AuthenticationPrincipal String username
    ) {
        log.info(username);
        return service.readNegotiationPaged(itemId, pageNumber > 0 ? pageNumber - 1 : 0, pageSize, username);
    }

    @PutMapping("/{proposalId}")
    public ResponseDto update(@PathVariable("itemId") Long itemId, @PathVariable("proposalId") Long proposalId, @Valid @RequestBody NegotiationDto.CreateAndUpdateRequest requestDto, @AuthenticationPrincipal String username) {
        return service.negotiationUpdate(itemId, proposalId, requestDto, username);
    }

    @DeleteMapping("/{proposalId}")
    public ResponseDto delete(@PathVariable("itemId") Long itemId, @PathVariable("proposalId") Long proposalId, @AuthenticationPrincipal String username) {
        return service.negotiationDelete(itemId, proposalId, username);
    }
}
