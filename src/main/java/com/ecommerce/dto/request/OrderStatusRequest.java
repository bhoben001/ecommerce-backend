package com.ecommerce.dto.request;

import com.ecommerce.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderStatusRequest {
    @NotNull(message = "status is required")
    private OrderStatus status;
}
