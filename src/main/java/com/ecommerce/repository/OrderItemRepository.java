package com.ecommerce.repository;

import com.ecommerce.dto.response.TopProductResponse;
import com.ecommerce.entity.OrderItem;
import com.ecommerce.enums.OrderStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("""
        select new com.ecommerce.dto.response.TopProductResponse(
            oi.product.id, max(oi.productName), sum(oi.quantity), sum(oi.lineTotal))
        from OrderItem oi
        where oi.order.status <> :status
        group by oi.product.id
        order by sum(oi.quantity) desc
        """)
    List<TopProductResponse> findTopProducts(@Param("status") OrderStatus status, Pageable pageable);

    @Query("""
    select count(oi) > 0 from OrderItem oi
    where oi.order.user.id = :userId
      and oi.product.id = :productId
      and oi.order.status = :status
    """)
    boolean hasPurchased(@Param("userId") Long userId,
                         @Param("productId") Long productId,
                         @Param("status") OrderStatus status);
}
