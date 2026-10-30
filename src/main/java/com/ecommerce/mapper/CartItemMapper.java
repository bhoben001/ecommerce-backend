package com.ecommerce.mapper;

import com.ecommerce.dto.response.CartItemResponse;
import com.ecommerce.entity.CartItem;
import com.ecommerce.entity.Product;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class CartItemMapper {
    public CartItemResponse toResponse(CartItem item) {
        Product p = item.getProduct();
        BigDecimal price = p.getDiscountPrice() != null ? p.getDiscountPrice() : p.getPrice();
        int quantity=item.getQuantity();

        CartItemResponse response = new CartItemResponse();

        response.setId(item.getId());
        response.setCartId(item.getCart().getId());
        response.setProductId(item.getProduct().getId());
        response.setProductName(item.getProduct().getName());
        response.setThumbnailUrl(item.getProduct().getThumbnailUrl());
        response.setQuantity(quantity);
        response.setUnitPrice(price);
        response.setTotalCost(price.multiply(new BigDecimal(quantity)));
        return response;
    }
}
