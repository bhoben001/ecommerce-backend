package com.ecommerce.mapper;

import com.ecommerce.dto.response.OrderItemResponse;
import com.ecommerce.dto.response.OrderResponse;
import com.ecommerce.entity.Order;
import com.ecommerce.entity.OrderItem;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    public OrderResponse toResponse(Order o, boolean withItems) {
        OrderResponse r = new OrderResponse();
        r.setId(o.getId());
        r.setOrderNumber(o.getOrderNumber());
        r.setStatus(o.getStatus());
        r.setPaymentStatus(o.getPaymentStatus());
        r.setShippingFullName(o.getShippingFullName());
        r.setShippingPhone(o.getShippingPhone());
        r.setShippingAddressLine(o.getShippingAddressLine());
        r.setShippingCity(o.getShippingCity());
        r.setShippingState(o.getShippingState());
        r.setShippingPincode(o.getShippingPincode());
        r.setSubtotal(o.getSubtotal());
        r.setDiscountAmount(o.getDiscountAmount());
        r.setShippingFee(o.getShippingFee());
        r.setTaxAmount(o.getTaxAmount());
        r.setTotalAmount(o.getTotalAmount());
        r.setPlacedAt(o.getPlacedAt());

        if (withItems) {
            r.setItems(o.getItems().stream().map(this::toItemResponse).toList());
        }
        return r;
    }

    private OrderItemResponse toItemResponse(OrderItem i) {
        OrderItemResponse r = new OrderItemResponse();
        r.setId(i.getId());
        r.setProductId(i.getProduct().getId());
        r.setProductName(i.getProductName());
        r.setUnitPrice(i.getUnitPrice());
        r.setQuantity(i.getQuantity());
        r.setLineTotal(i.getLineTotal());
        return r;
    }
}
