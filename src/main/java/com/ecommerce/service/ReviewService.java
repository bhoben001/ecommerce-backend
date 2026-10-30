package com.ecommerce.service;

import com.ecommerce.security.UserPrincipal;
import com.ecommerce.dto.request.ReviewRequest;
import com.ecommerce.dto.request.ReviewUpdateRequest;
import com.ecommerce.dto.response.ReviewResponse;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.Review;
import com.ecommerce.entity.User;
import com.ecommerce.enums.OrderStatus;
import com.ecommerce.exception.ConflictException;
import com.ecommerce.exception.ForbiddenException;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.mapper.ReviewMapper;
import com.ecommerce.repository.OrderItemRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.ReviewRepository;
import com.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ReviewMapper  reviewMapper;

    @Transactional
    public ReviewResponse createReview(UserPrincipal me, ReviewRequest request) {

        if (!orderItemRepository.hasPurchased(me.getId(), request.getProductId(), OrderStatus.DELIVERED))
            throw new ForbiddenException("You can review only the products you have purchased and received");

        if (reviewRepository.existsByUserIdAndProductId(me.getId(), request.getProductId()))
            throw new ConflictException("You have already reviewed this product");


        User user=userRepository.findById(me.getId()).get();
        Product product=productRepository.findById(request.getProductId()).orElseThrow(()->
                new ResourceNotFoundException("Product not found"));
        Review review=new Review();
        review.setUser(user);
        review.setProduct(product);
        if(request.getRating()!=null)review.setRating(request.getRating());
        if(request.getComment()!=null)review.setComment(request.getComment());
        Review save = reviewRepository.save(review);
        reviewRepository.flush();
        updateAverageRating(product);
        return reviewMapper.toResponse(save);
    }

    @Transactional(readOnly=true)
    public Page<ReviewResponse> list(Long productId, Pageable pageable) {
        Page<Review> list=reviewRepository.findByProductId(productId,pageable);
        return list.map(reviewMapper::toResponse);
    }

    @Transactional
    public ReviewResponse editReview(UserPrincipal me, Long id, ReviewUpdateRequest request) {
        Review review=reviewRepository.findByUserIdAndId(me.getId(),id)
                .orElseThrow(()->new ResourceNotFoundException("Review not found"));
        if(request.getComment()!=null)review.setComment(request.getComment());
        if(request.getRating()!=null)review.setRating(request.getRating());
        Review saved = reviewRepository.save(review);
        reviewRepository.flush();
        updateAverageRating(review.getProduct());
        return  reviewMapper.toResponse(saved);
    }

    @Transactional
    public void deleteReview(UserPrincipal me, Long reviewId){
        Review review = reviewRepository.findById(reviewId).orElseThrow(() ->
                new ResourceNotFoundException("Review not found"));

        boolean isAdmin = me.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !review.getUser().getId().equals(me.getId()))
            throw new ForbiddenException("You are not allowed to delete this review");

        Product product = review.getProduct();
        reviewRepository.delete(review);
        reviewRepository.flush();
        updateAverageRating(product);
    }

    private void updateAverageRating(Product product) {
        Double avg = reviewRepository.findAverageRating(product.getId());
        product.setAverageRating(Math.round(avg * 10.0) / 10.0);
    }
}
