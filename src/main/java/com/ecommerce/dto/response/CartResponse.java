package com.ecommerce.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
public class CartResponse {

    private Long id;

    private Long userId;

    private List<CartItemResponse> items=new ArrayList<>();

    private BigDecimal totalCost;

    private LocalDateTime createdAt;


}
