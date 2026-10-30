package com.ecommerce.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReviewResponse {

    private Long id;

    private Long userId;

    private String userName;

    private Long productId;

    private String productName;

    private Integer rating;

    private String comment;

    private LocalDateTime createdAt;
}
