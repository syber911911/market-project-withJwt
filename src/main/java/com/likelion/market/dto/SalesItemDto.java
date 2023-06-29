package com.likelion.market.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.likelion.market.entity.SalesItemEntity;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.List;

public class SalesItemDto {
    @Data
    public static class Response {
        String message;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ReadByIdResponse {
        private String title;
        private String description;
        private Long minPriceWanted;
        private String imageUrl;
        private String status;

        public static ReadByIdResponse fromEntity(SalesItemEntity entity) {
            ReadByIdResponse dto = new ReadByIdResponse();
            dto.setTitle(entity.getTitle());
            dto.setDescription(entity.getDescription());
            dto.setMinPriceWanted(entity.getMinPriceWanted());
            dto.setImageUrl(entity.getImageUrl());
            dto.setStatus(entity.getStatus());
            return dto;
        }
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ReadAllResponse {
        private Long id;
        private String title;
        private String description;
        private Long minPriceWanted;
        private String imageUrl;
        private String status;

        public static ReadAllResponse fromEntity(SalesItemEntity entity) {
            ReadAllResponse dto = new ReadAllResponse();
            dto.setId(entity.getId());
            dto.setTitle(entity.getTitle());
            dto.setDescription(entity.getDescription());
            dto.setMinPriceWanted(entity.getMinPriceWanted());
            dto.setImageUrl(entity.getImageUrl());
            dto.setStatus(entity.getStatus());
            return dto;
        }
    }

    @Data
    public static class CreateRequest {
        private String title;
        private String description;
        private Long minPriceWanted;
        private String imageUrl;
        private String status;
        private String writer;
        private String password;
    }
}
