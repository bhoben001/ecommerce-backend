package com.ecommerce.repository;

import com.ecommerce.entity.WishlistItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WishListItemRepository extends JpaRepository<WishlistItem, Long> {
    Page<WishlistItem> findByUserId(long userId, Pageable pageable);
}
