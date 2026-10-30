package com.ecommerce.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Product {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition ="TEXT")
    private String description;

    @Column(nullable = false)
    @Positive(message = "price must be greater than 0")
    private BigDecimal price;

    private BigDecimal discountPrice;

    @Column(unique = true)
    private String sku;

    @Min(value = 0, message = "Stock quantity cannot be less than 0")
    @Column(nullable = false)
    private Integer stockQuantity;

    private String brand;

    private String thumbnailUrl;
    @ManyToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name="category_id", nullable = false)
    private Category category;

    @OneToMany(mappedBy = "product",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<ProductImage> images=new ArrayList<>();

    private Double averageRating;

    private Boolean active=true;

    @CreationTimestamp()
    @Column( updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
