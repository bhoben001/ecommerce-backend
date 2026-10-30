package com.ecommerce.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlaceOrderRequest {
    @NotNull(message = "addressId is required")
    private Long addressId;
}
