package com.likelion.market.negotiation.entity;

import com.likelion.market.salesItem.entity.SalesItemEntity;
import com.likelion.market.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "negotiation")
public class NegotiationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer suggestedPrice;
    private String status;
    private String writer;
    private String password;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private SalesItemEntity salesItem;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
}
