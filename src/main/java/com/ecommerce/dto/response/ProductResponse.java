package com.ecommerce.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ProductResponse {
    private Long Id;

    private String name;

    private String description;

    private BigDecimal price;

    private BigDecimal discountPrice;

    private String sku;

    private Integer stockQuantity;

    private String brand;

    private String thumbnailUrl;  //Main image

    private Long categoryId;

    private String categoryName;

    private List<ProductImageResponse> images=new ArrayList<>();

    private BigDecimal finalPrice;

    private Double averageRating=0.0;

    private Boolean active=true;

    private LocalDateTime createdAt;
}
