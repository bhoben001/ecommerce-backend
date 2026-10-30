package com.ecommerce.controller;

import com.ecommerce.security.UserPrincipal;
import com.ecommerce.dto.request.OrderStatusRequest;
import com.ecommerce.dto.request.PlaceOrderRequest;
import com.ecommerce.dto.response.OrderResponse;
import com.ecommerce.enums.OrderStatus;
import com.ecommerce.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
 
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse placeOrder(@AuthenticationPrincipal UserPrincipal me,
                                    @Valid @RequestBody PlaceOrderRequest request) {
        return orderService.placeOrder(me, request);
    }

    @GetMapping("/my")
    public Page<OrderResponse> getMyOrders(@AuthenticationPrincipal UserPrincipal me,
                                           @PageableDefault(size = 10, sort = "placedAt",
                                                   direction = Sort.Direction.DESC) Pageable pageable) {
        return orderService.getMyOrders(me, pageable);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Page<OrderResponse> getAllOrders(@RequestParam(required = false) OrderStatus status,
                                            @PageableDefault(size = 10, sort = "placedAt",
                                                    direction = Sort.Direction.DESC) Pageable pageable) {
        return orderService.getAllOrders(status, pageable);
    }

    @GetMapping("/{id}")
    public OrderResponse getOrder(@AuthenticationPrincipal UserPrincipal me, @PathVariable Long id) {
        return orderService.getOrder(me, id);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public OrderResponse updateStatus(@PathVariable Long id, @Valid @RequestBody OrderStatusRequest request) {
        return orderService.updateStatus(id, request);
    }

    @PatchMapping("/{id}/cancel")
    public OrderResponse cancelOrder(@AuthenticationPrincipal UserPrincipal me, @PathVariable Long id) {
        return orderService.cancelOrder(me, id);
    }
}
