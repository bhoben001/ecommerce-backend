package com.ecommerce.controller;

import com.ecommerce.security.UserPrincipal;
import com.ecommerce.dto.request.CartItemRequest;
import com.ecommerce.dto.response.CartItemResponse;
import com.ecommerce.dto.response.CartResponse;
import com.ecommerce.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping()
    public CartResponse getCart(@AuthenticationPrincipal UserPrincipal me){
        return cartService.getCart(me);
    }

    @PostMapping("/items")
    @ResponseStatus(HttpStatus.CREATED)
    public void addItemToCart(@AuthenticationPrincipal UserPrincipal me, @Valid @RequestBody CartItemRequest request){
        cartService.addItemToCart(me,request);
    }

    @PutMapping("/items/{itemId}")
    public CartItemResponse changeQuantityOfItem(@AuthenticationPrincipal UserPrincipal me,
                                                 @PathVariable Long itemId,
                                                 @RequestBody Integer quantity){
        return cartService.changeQuantity(me,itemId,quantity);
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeItem(@AuthenticationPrincipal UserPrincipal me, @PathVariable Long itemId){
        cartService.removeItem(me,itemId);
    }

    @DeleteMapping()
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCart(@AuthenticationPrincipal UserPrincipal me){
        cartService.deleteCart(me);
    }
}
