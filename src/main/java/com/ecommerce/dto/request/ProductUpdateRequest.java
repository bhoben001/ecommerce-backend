package com.ecommerce.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
public class ProductUpdateRequest {

    private String name;

    private String description;

    @Positive(message = "price must be greater than 0")
    private BigDecimal price;

    @Positive
    private BigDecimal discountPrice;

    private String sku;

    @Min(value = 0, message = "Stock quantity cannot be less than 0")
    private Integer stockQuantity;

    private String brand;

    private String thumbnailUrl;  //Main image

    private Long categoryId;
}
