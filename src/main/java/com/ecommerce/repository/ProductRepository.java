package com.ecommerce.repository;

import com.ecommerce.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Boolean existsBySku(String sku);

    @Modifying
    @Query("update Product p set p.stockQuantity = p.stockQuantity + :delta " +
          "where p.id = :id and p.stockQuantity + :delta >= 0")
    int adjustStock(@Param("id") Long id, @Param("delta") int delta);

    long countByActiveTrue();

    boolean existsBySkuAndIdNot(String sku, Long id);

    @EntityGraph(attributePaths = "category")
    Page<Product> findByActiveTrueAndStockQuantityLessThanEqual(Integer threshold, Pageable pageable);

    Page<Product> findAllByActiveTrue(Pageable pageable);

    @EntityGraph(attributePaths = "category")
    @Query("""
    select p from Product p
    where p.active = true
      and (:keyword is null or lower(p.name) like :keyword or lower(p.description) like :keyword)
      and (:categoryId is null or p.category.id = :categoryId)
      and (:brand is null or lower(p.brand) = :brand)
      and (:minPrice is null or coalesce(p.discountPrice, p.price) >= :minPrice)
      and (:maxPrice is null or coalesce(p.discountPrice, p.price) <= :maxPrice)
    """)
    Page<Product> search(@Param("keyword") String keyword,
                         @Param("categoryId") Long categoryId,
                         @Param("brand") String brand,
                         @Param("minPrice") BigDecimal minPrice,
                         @Param("maxPrice") BigDecimal maxPrice,
                         Pageable pageable);
}
