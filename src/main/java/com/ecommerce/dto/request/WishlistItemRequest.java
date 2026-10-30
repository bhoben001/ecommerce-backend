package com.ecommerce.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WishlistItemRequest {

    @NotNull
    private Long productId;

}
