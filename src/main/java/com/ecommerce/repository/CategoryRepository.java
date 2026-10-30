package com.ecommerce.repository;

import com.ecommerce.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String name);

    boolean existsByParentIdAndActiveTrue(Long id);

    boolean existsByNameAndIdNot(String name, Long id);

    List<Category> findByParentIsNullAndActiveTrue();

    Page<Category> findByActiveTrue(Pageable pageable);
}
