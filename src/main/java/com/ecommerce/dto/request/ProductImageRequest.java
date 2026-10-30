package com.ecommerce.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductImageRequest {

    @NotEmpty
    private String imageUrl;

    private Boolean isPrimary=false;

    private Integer displayOrder;
}
