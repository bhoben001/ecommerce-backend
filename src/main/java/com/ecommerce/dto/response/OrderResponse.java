package com.ecommerce.dto.response;

import com.ecommerce.enums.OrderStatus;
import com.ecommerce.enums.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class OrderResponse {
    private Long id;
    private String orderNumber;
    private OrderStatus status;
    private PaymentStatus paymentStatus;

    private String shippingFullName;
    private String shippingPhone;
    private String shippingAddressLine;
    private String shippingCity;
    private String shippingState;
    private String shippingPincode;

    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal shippingFee;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private LocalDateTime placedAt;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<OrderItemResponse> items = new ArrayList<>();
}
