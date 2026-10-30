package com.ecommerce.mapper;

import com.ecommerce.dto.response.WishlistItemResponse;
import com.ecommerce.entity.WishlistItem;
import org.springframework.stereotype.Component;

@Component
public class WishListItemMapper {
    public WishlistItemResponse toResponse(WishlistItem w) {
        WishlistItemResponse r = new WishlistItemResponse();
        r.setId(w.getId());
        r.setUserId(w.getUser().getId());
        r.setProductId(w.getProduct().getId());
        r.setProductName(w.getProduct().getName());
        r.setAddedAt(w.getAddedAt());
        return r;
    }
}
