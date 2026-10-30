package com.ecommerce.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryRequest {

    @NotBlank(message = "name is required")
    @Size(max = 100, message = "name can be at most 100 characters")
    private String name;

    @Size(max = 500, message = "description can be at most 500 characters")
    private String description;

    private String imageUrl;

    private Long parentId;

}
