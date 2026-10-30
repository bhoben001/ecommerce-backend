package com.ecommerce.mapper;

import com.ecommerce.dto.response.ReviewResponse;
import com.ecommerce.entity.Review;
import org.springframework.stereotype.Component;

@Component
public class ReviewMapper {
    public ReviewResponse toResponse(Review review) {
        ReviewResponse r = new ReviewResponse();
        r.setId(review.getId());
        r.setUserId(review.getUser().getId());
        r.setUserName((review.getUser().getFullName()));
        r.setProductId(review.getProduct().getId());
        r.setProductName(review.getProduct().getName());
        r.setRating(review.getRating());
        r.setComment(review.getComment());
        r.setCreatedAt(review.getCreatedAt());
        return r;
    }

}
