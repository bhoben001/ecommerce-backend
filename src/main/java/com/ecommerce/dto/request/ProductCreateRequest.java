package com.ecommerce.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductCreateRequest {

    @NotBlank
    private String name;

    private String description;

    @NotNull @Positive
    private BigDecimal price;

    @Positive
    private BigDecimal discountPrice;

    @NotBlank
    private String sku;

    @NotNull @Min(0)
    private Integer stockQuantity;

    @NotBlank
    private String brand;

    private String thumbnailUrl;

    @NotNull
    private Long categoryId;
}
