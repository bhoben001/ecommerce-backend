package com.ecommerce.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductImageResponse {

    private Long id;

    private Long productId;

    private String imageUrl;

    private Boolean isPrimary=false;

    private Integer displayOrder;

}
