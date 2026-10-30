package com.ecommerce.mapper;

import com.ecommerce.dto.response.CartItemResponse;
import com.ecommerce.dto.response.CartResponse;
import com.ecommerce.entity.Cart;
import com.ecommerce.entity.CartItem;
import com.ecommerce.entity.Product;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class CartMapper {

    public CartResponse toResponse(Cart cart) {
        List<CartItemResponse> items = cart.getItems().stream().map(this::toItemResponse).toList();

        CartResponse r = new CartResponse();
        r.setId(cart.getId());
        r.setUserId(cart.getUser().getId());
        r.setItems(items);
        r.setCreatedAt(cart.getCreatedAt());
        r.setTotalCost(items.stream()
                .map(CartItemResponse::getTotalCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        return r;
    }

    private CartItemResponse toItemResponse(CartItem i) {

        Product p = i.getProduct();
        BigDecimal price = p.getDiscountPrice() != null ? p.getDiscountPrice() : p.getPrice();

        CartItemResponse response = new CartItemResponse();
        response.setId(i.getId());
        response.setProductId(i.getProduct().getId());
        response.setProductName(i.getProduct().getName());
        response.setThumbnailUrl(i.getProduct().getThumbnailUrl());
        response.setUnitPrice(price);
        response.setQuantity(i.getQuantity());
        response.setTotalCost(price.multiply(BigDecimal.valueOf(i.getQuantity())));
        return response;
    }
}
