package com.ecommerce.service;

import com.ecommerce.dto.request.CategoryRequest;
import com.ecommerce.dto.response.CategoryResponse;
import com.ecommerce.entity.Category;
import com.ecommerce.exception.BadRequestException;
import com.ecommerce.exception.ConflictException;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.mapper.CategoryMapper;
import com.ecommerce.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        String name = request.getName().trim();
        if (categoryRepository.existsByName(name)) {
            throw new ConflictException("Category already exists: " + name);
        }

        Category category = new Category();
        category.setName(name);
        category.setDescription(request.getDescription());
        category.setImageUrl(request.getImageUrl());
        category.setParent(resolveParent(request.getParentId(), null));

        return categoryMapper.toResponse(categoryRepository.save(category), false);
    }

    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest  request) {
        Category category = findActive(id);

        String name = request.getName().trim();
        if (categoryRepository.existsByNameAndIdNot(name, id)) {
            throw new ConflictException("Category already exists: " + name);
        }

        category.setName(name);
        category.setDescription(request.getDescription());
        category.setImageUrl(request.getImageUrl());
        category.setParent(resolveParent(request.getParentId(), id));

        return categoryMapper.toResponse(category, false);
    }

    @Transactional(readOnly = true)
    public Page<CategoryResponse> list(Pageable pageable) {
        return categoryRepository.findByActiveTrue(pageable)
                .map(c -> categoryMapper.toResponse(c, false));
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategoryTree() {
        return categoryRepository.findByParentIsNullAndActiveTrue().stream()
                .map(c -> categoryMapper.toResponse(c, true))
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponse getCategory(Long id) {
        return categoryMapper.toResponse(findActive(id), false);
    }

    @Transactional
    public void deactivateCategory(Long id) {
        Category category = findActive(id);
        if (categoryRepository.existsByParentIdAndActiveTrue(id)) {
            throw new BadRequestException("Deactivate its sub-categories first");
        }
        category.setActive(false);
    }

    private Category resolveParent(Long parentId, Long selfId) {
        if (parentId == null) return null;

        Category parent = findActive(parentId);
        for (Category c = parent; c != null; c = c.getParent()) {
            if (c.getId().equals(selfId)) {
                throw new BadRequestException("Category cannot be its own parent or sub-category");
            }
        }
        return parent;
    }

    private Category findActive(Long id) {
        return categoryRepository.findById(id)
                .filter(c -> Boolean.TRUE.equals(c.getActive()))
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }
}
