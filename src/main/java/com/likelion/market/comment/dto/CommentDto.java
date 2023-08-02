package com.likelion.market.comment.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.likelion.market.comment.entity.CommentEntity;
import lombok.Data;

@Data
public class CommentDto {
    @Data
    // comment 생성 및 update 시에 사용자가 보내는 requestBody 를 받는 Dto
    public static class CreateAndUpdateCommentRequest{
        String content;
    }

    @Data
    // comment 에 reply 를 추가할 때 사용자가 보내는 requestBody 를 받는 Dto
    public static class UpdateReplyRequest {
        String reply;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    // read comment 를 하는 경우 사용자에게 제공될 Dto
    public static class ReadCommentsResponse {
        Long id;
        String content;
        String reply;

        public static ReadCommentsResponse fromEntity(CommentEntity entity) {
            ReadCommentsResponse readCommentsResponse = new ReadCommentsResponse();
            readCommentsResponse.setId(entity.getId());
            readCommentsResponse.setContent(entity.getContent());
            readCommentsResponse.setReply(entity.getReply());
            return readCommentsResponse;
        }
    }
}
