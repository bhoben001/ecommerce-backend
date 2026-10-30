package com.ecommerce.mapper;

import com.ecommerce.dto.response.CategoryResponse;
import com.ecommerce.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public CategoryResponse toResponse(Category category, Boolean withChildren){
        CategoryResponse response=new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setDescription(category.getDescription());
        response.setImageUrl(category.getImageUrl());
        response.setParentId(category.getParent()!=null ? category.getParent().getId():null);
        response.setActive(category.getActive());
        response.setCreatedAt(category.getCreatedAt());

        if (withChildren) {
            response.setChildren(category.getChildren().stream()
                    .filter(child -> Boolean.TRUE.equals(child.getActive()))
                    .map(child -> toResponse(child, true))
                    .toList());
        }
        return response;
    }

}
