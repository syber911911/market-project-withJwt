package com.likelion.market.salesItem.entity;

import com.likelion.market.user.entity.UserEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "sales_item")
public class SalesItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "title must not be null")
    private String title;
    private String description;
    private String imageUrl;

    @NotNull(message = "price must not be null")
    private Long minPriceWanted;
    private String status;
    private String writer;
    private String password;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
}
