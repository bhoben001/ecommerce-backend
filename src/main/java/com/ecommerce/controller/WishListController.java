package com.ecommerce.controller;

import com.ecommerce.security.UserPrincipal;
import com.ecommerce.dto.request.WishlistItemRequest;
import com.ecommerce.dto.response.WishlistItemResponse;
import com.ecommerce.service.WishListService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/wishlist")
public class WishListController {

    private final WishListService  wishListService;
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public WishlistItemResponse addItem(@AuthenticationPrincipal UserPrincipal me, @Valid @RequestBody WishlistItemRequest wishlistItem) {
        return wishListService.addItem(me, wishlistItem);
    }
    @GetMapping()
    public Page<WishlistItemResponse> getWishList(@AuthenticationPrincipal UserPrincipal me,@PageableDefault Pageable pageable) {
        return wishListService.list(me,pageable);
    }

    @DeleteMapping("/{wishListId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal UserPrincipal me,@PathVariable Long wishListId) {
        wishListService.deleteWish(me,wishListId);
    }
}
