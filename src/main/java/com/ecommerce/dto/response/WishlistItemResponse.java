package com.ecommerce.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class WishlistItemResponse {

    private Long id;

    private Long userId;

    private Long productId;

    private String productName;

    private LocalDateTime addedAt;
}
