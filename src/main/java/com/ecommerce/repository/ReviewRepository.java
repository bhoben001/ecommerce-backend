package com.ecommerce.repository;

import com.ecommerce.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByProductId(Long productId, Pageable pageable);
    Optional<Review> findByUserIdAndId(Long userId, Long id);
    boolean existsByUserIdAndProductId(Long userId, Long productId);
    @Query("select coalesce(avg(r.rating), 0) from Review r where r.product.id = :productId")
    Double findAverageRating(@Param("productId") Long productId);

}
