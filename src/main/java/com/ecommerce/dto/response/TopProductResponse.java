package com.ecommerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class TopProductResponse {
    Long productId;
    String productName;
    Long unitsSold;
    BigDecimal revenue;
}
