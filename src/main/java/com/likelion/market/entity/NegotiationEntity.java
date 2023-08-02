package com.likelion.market.entity;

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
