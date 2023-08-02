package com.likelion.market.comment.service;

import com.likelion.market.comment.dto.CommentDto;
import com.likelion.market.global.dto.PageDto;
import com.likelion.market.global.dto.ResponseDto;
import com.likelion.market.comment.entity.CommentEntity;
import com.likelion.market.salesItem.entity.SalesItemEntity;
import com.likelion.market.salesItem.service.SalesItemService;
import com.likelion.market.user.entity.UserEntity;
import com.likelion.market.user.exception.UserException;
import com.likelion.market.user.exception.UserExceptionType;
import com.likelion.market.comment.repository.CommentRepository;
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
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final JpaUserDetailsManager jpaUserDetailsManager;
    private final SalesItemService salesItemService;

    // create comment
    public ResponseDto createComment(Long itemId, CommentDto.CreateAndUpdateCommentRequest requestDto, String username) {
        // 해당 item 글 조회
        SalesItemEntity salesItem = salesItemService.getSalesItem(itemId);
        // 해당 user 조회
        UserEntity user = jpaUserDetailsManager.getUser(username);

        CommentEntity comment = new CommentEntity();
        comment.setSalesItem(salesItem);
        comment.setContent(requestDto.getContent());
        comment.setUser(user);
        commentRepository.save(comment);

        ResponseDto response = new ResponseDto();
        response.setMessage("댓글이 등록되었습니다.");
        return response;
    }

    // readAll comment
    public PageDto<CommentDto.ReadCommentsResponse> readCommentPaged(Long itemId, Integer pageNumber, Integer pageSize) {
        // 해당 item 조회
        SalesItemEntity salesItem = salesItemService.getSalesItem(itemId);

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("id"));
        Page<CommentEntity> commentEntityPage = commentRepository.findAllBySalesItem(salesItem, pageable);
        Page<CommentDto.ReadCommentsResponse> originCommentDtoPage = commentEntityPage.map(CommentDto.ReadCommentsResponse::fromEntity);
        PageDto<CommentDto.ReadCommentsResponse> pageDto = new PageDto<>();
        return pageDto.makePage(originCommentDtoPage);
    }

    // update comment
    public ResponseDto updateComment(Long itemId, Long commentId, CommentDto.CreateAndUpdateCommentRequest requestDto, String username) {
        // 해당 item 조회
        SalesItemEntity salesItem = salesItemService.getSalesItem(itemId);
        // 해당 comment 조회
        CommentEntity comment = this.getComment(commentId);
        // 해당 user 조회
        UserEntity user = jpaUserDetailsManager.getUser(username);

        if (comment.getSalesItem().equals(salesItem)) {
            // 해당 item 과 comment 가 서로 연관관계가 있는지 확인
            if (comment.getUser().equals(user)) {
                // 해당 comment 작성자가 보낸 요청인지 확인
                comment.setContent(requestDto.getContent());
                commentRepository.save(comment);

                ResponseDto response = new ResponseDto();
                response.setMessage("댓글이 수정되었습니다.");
                return response;
            } else throw new UserException(UserExceptionType.WRONG_USER); // 해당 user 가 작성한 댓글이 아님
        } else throw new ResponseStatusException(HttpStatus.BAD_REQUEST); // 댓글이 해당 아이템의 댓글이 아님
    }

    // update reply
    public ResponseDto updateReply(Long itemId, Long commentId, CommentDto.UpdateReplyRequest requestDto, String username) {
        // 해당 item 조회
        SalesItemEntity salesItem = salesItemService.getSalesItem(itemId);
        // 해당 comment 조회
        CommentEntity comment = this.getComment(commentId);
        // 해당 user 조회
        UserEntity user = jpaUserDetailsManager.getUser(username);

        if (comment.getSalesItem().equals(salesItem)) {
            // 해당 item 과 comment 가 서로 연관관계가 있는 확인
            if (salesItem.getUser().equals(user)) {
                // 해당 item 을 등록한 사용자가 보낸 요청인지 확인
                comment.setReply(requestDto.getReply());
                commentRepository.save(comment);

                ResponseDto response = new ResponseDto();
                response.setMessage("댓글에 답변이 추가되었습니다.");
                return response;
            } else throw new UserException(UserExceptionType.WRONG_USER); // 해당 아이템 작성자가 아님
        } else throw new ResponseStatusException(HttpStatus.BAD_REQUEST); // 댓글이 해당 아이템의 댓글이 아님
    }

    // delete
    public ResponseDto deleteComment(Long itemId, Long commentId, String username) {
        // 해당 item 조회
        SalesItemEntity salesItem = salesItemService.getSalesItem(itemId);
        // 해당 comment 조회
        CommentEntity comment = this.getComment(commentId);
        // 해당 user 조회
        UserEntity user = jpaUserDetailsManager.getUser(username);

        if (comment.getSalesItem().equals(salesItem)) {
            // 해당 item 과 comment 가 연관관계가 있는지 확인
            if (comment.getUser().equals(user)) {
                // comment 를 작성한 사용자가 보낸 요청인지 확인
                commentRepository.delete(comment);

                ResponseDto response = new ResponseDto();
                response.setMessage("댓글을 삭제했습니다.");
                return response;
            } throw new UserException(UserExceptionType.WRONG_USER); // 해당 댓글을 작성한 사용자가 아님
        } throw new ResponseStatusException(HttpStatus.BAD_REQUEST); // 해당 아이템의 댓글이 아님
    }

    // commentId 를 통해 commentEntity 를 조회 후 반환하는 메서드
    public CommentEntity getComment(Long commentId) {
        Optional<CommentEntity> optionalComment = commentRepository.findById(commentId);
        if (optionalComment.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND); // 댓글이 존재하지 않음
        return optionalComment.get();
    }

    // update user
//    public ResponseDto updateUser(Long itemId, Long commentId, UserDto.UpdateUserRequest requestDto) {
//        if (!salesItemRepository.existsById(itemId))
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND); // 아이템 존재하지 않음
//
//        Optional<CommentEntity> optionalComment = commentRepository.findById(commentId);
//
//        if (optionalComment.isPresent()) {
//            CommentEntity comment = optionalComment.get();
//            if (comment.getItemId().equals(itemId)) {
//                if (comment.getWriter().equals(requestDto.getRecentUser().getWriter())
//                        && comment.getPassword().equals(requestDto.getRecentUser().getPassword())) {
//                    comment.setWriter(requestDto.getUpdateUser().getWriter());
//                    comment.setPassword(requestDto.getUpdateUser().getPassword());
//                    commentRepository.save(comment);
//
//                    ResponseDto response = new ResponseDto();
//                    response.setMessage("댓글 작성자 정보가 수정되었습니다.");
//                    return response;
//                } else throw new ResponseStatusException(HttpStatus.UNAUTHORIZED); // 인증 오류
//            } else throw new ResponseStatusException(HttpStatus.BAD_REQUEST); // 댓글이 해당 아이템의 댓글이 아님
//        } else throw new ResponseStatusException(HttpStatus.NOT_FOUND); // 댓글 존재하지 않음
//    }
}
