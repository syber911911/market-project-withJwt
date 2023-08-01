package com.likelion.market.entity;

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

    @NotNull(message = "writer must not be null")
    private String writer;

    @NotNull(message = "password must not be null")
    private String password;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
}
