package com.likelion.market.dto;

import lombok.Data;

@Data
public class NegotiationDto {
    @Data
    public static class CreateAndUpdateRequest {
        private String writer;
        private String password;
        private Integer suggestedPrice;
    }
}
