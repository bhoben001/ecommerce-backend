package com.ecommerce.mapper;

import com.ecommerce.dto.response.ProductImageResponse;
import com.ecommerce.dto.response.ProductResponse;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.ProductImage;
import org.springframework.stereotype.Component;

import java.util.Comparator;

@Component
public class ProductMapper {

    public ProductResponse toResponse(Product p, boolean withImages) {
        ProductResponse r = new ProductResponse();
        r.setId(p.getId());
        r.setName(p.getName());
        r.setDescription(p.getDescription());
        r.setPrice(p.getPrice());
        r.setDiscountPrice(p.getDiscountPrice());
        r.setFinalPrice(p.getDiscountPrice() != null ? p.getDiscountPrice() : p.getPrice());
        r.setSku(p.getSku());
        r.setStockQuantity(p.getStockQuantity());
        r.setBrand(p.getBrand());
        r.setThumbnailUrl(p.getThumbnailUrl());
        r.setCategoryId(p.getCategory().getId());
        r.setCategoryName(p.getCategory().getName());
        r.setAverageRating(p.getAverageRating());
        r.setActive(p.getActive());
        r.setCreatedAt(p.getCreatedAt());

        if (withImages) {
            r.setImages(p.getImages().stream()
                    .sorted(
                            Comparator.comparing(ProductImage::getDisplayOrder,
                            Comparator.nullsLast(Comparator.naturalOrder()))
                    )
                    .map(this::toImageResponse)
                    .toList());
        }
        return r;
    }

    private ProductImageResponse toImageResponse(ProductImage i) {
        ProductImageResponse r = new ProductImageResponse();
        r.setId(i.getId());
        r.setProductId(i.getProduct().getId());
        r.setImageUrl(i.getImageUrl());
        r.setIsPrimary(i.getIsPrimary());
        r.setDisplayOrder(i.getDisplayOrder());
        return r;
    }
}
