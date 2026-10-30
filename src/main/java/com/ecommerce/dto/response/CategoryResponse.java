package com.ecommerce.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CategoryResponse {

    private Long id;

    private String name;

    private String description;

    private String imageUrl;

    private Long parentId;

    private List<CategoryResponse> children = new ArrayList<>();

    private Boolean active;

    private LocalDateTime createdAt;
}
