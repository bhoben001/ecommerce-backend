package com.ecommerce.dto.response;

import lombok.Getter;
import lombok.Setter;


import java.math.BigDecimal;

@Setter
@Getter
public class CartItemResponse {

    private long id;

    private long cartId;

    private long productId;

    private String productName;

    private String thumbnailUrl;

    private Integer quantity;

    private BigDecimal unitPrice;

    private BigDecimal totalCost;

}
