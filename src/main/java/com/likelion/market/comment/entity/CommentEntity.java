package com.likelion.market.comment.entity;

import com.likelion.market.salesItem.entity.SalesItemEntity;
import com.likelion.market.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "comments")
public class CommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String writer;
    private String password;
    private String content;
    private String reply;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private SalesItemEntity salesItem;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
}
