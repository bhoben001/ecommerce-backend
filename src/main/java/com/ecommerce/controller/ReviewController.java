package com.ecommerce.controller;

import com.ecommerce.security.UserPrincipal;
import com.ecommerce.dto.request.ReviewRequest;
import com.ecommerce.dto.request.ReviewUpdateRequest;
import com.ecommerce.dto.response.ReviewResponse;
import com.ecommerce.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewResponse createReview(@AuthenticationPrincipal UserPrincipal me, @Valid @RequestBody ReviewRequest request){
        return reviewService.createReview(me,request);
    }

    @GetMapping("/{productId}/reviews")
    public Page<ReviewResponse> reviewList(@PathVariable Long productId,@PageableDefault Pageable pageable){
        return reviewService.list(productId,pageable);
    }

    @PutMapping("/{id}")
    public ReviewResponse editReview(@AuthenticationPrincipal UserPrincipal me,
                                     @PathVariable Long id,
                                     @Valid @RequestBody ReviewUpdateRequest request){
        return reviewService.editReview(me,id,request);
    }
    @DeleteMapping("/{reviewId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReview(@AuthenticationPrincipal UserPrincipal me, @PathVariable Long reviewId){
        reviewService.deleteReview(me,reviewId);
    }
}
