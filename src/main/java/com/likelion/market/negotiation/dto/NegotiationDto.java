package com.likelion.market.negotiation.dto;

import com.likelion.market.negotiation.annotations.Status;
import com.likelion.market.negotiation.entity.NegotiationEntity;
import lombok.Data;

@Data
public class NegotiationDto {
    @Data
    // create 혹은 update 요청 시 사용자가 보낼 데이터를 담는 Dto
    public static class CreateAndUpdateRequest {
        private Integer suggestedPrice;
        @Status(statusList = {"수락", "거절", "확정"})
        private String status;
    }

    @Data
    // read 요청 시 사용자에게 반환될 데이터를 담는 Dto
    public static class ReadNegotiationResponse {
        private Long id;
        private Integer suggestedPrice;
        private String status;

        public static ReadNegotiationResponse fromEntity(NegotiationEntity entity) {
            ReadNegotiationResponse negotiation = new ReadNegotiationResponse();
            negotiation.setId(entity.getId());
            negotiation.setSuggestedPrice(entity.getSuggestedPrice());
            negotiation.setStatus(entity.getStatus());
            return negotiation;
        }
    }
}
