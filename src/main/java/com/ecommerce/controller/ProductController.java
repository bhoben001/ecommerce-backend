package com.ecommerce.controller;

import com.ecommerce.dto.request.ProductCreateRequest;
import com.ecommerce.dto.request.ProductImageRequest;
import com.ecommerce.dto.request.ProductUpdateRequest;
import com.ecommerce.dto.request.StockUpdateRequest;
import com.ecommerce.dto.response.ProductResponse;
import com.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponse createProduct(@Valid @RequestBody ProductCreateRequest request) {
        return productService.createProduct(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponse updateProduct(@PathVariable Long id, @Valid @RequestBody ProductUpdateRequest request) {
        return productService.updateProduct(id, request);
    }


    @GetMapping("/{id}")
    public ProductResponse getById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @GetMapping("/getAll")
    public Page<ProductResponse> getAllProducts(@PageableDefault  Pageable pageable) {
        return productService.getAllProducts(pageable);
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deactivateProduct(@PathVariable Long id) {
        productService.deactivateProduct(id);
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public void activateProduct(@PathVariable Long id) {
        productService.activateProduct(id);
    }

    @PatchMapping("/{id}/stock")
    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponse updateStock(@PathVariable Long id, @Valid @RequestBody StockUpdateRequest request) {
        return productService.updateStock(id, request);
    }

    @PostMapping("/{id}/images")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponse addImage(@PathVariable Long id, @Valid @RequestBody ProductImageRequest request) {
        return productService.addImage(id, request);
    }

    @PatchMapping("/{productId}/images/{imageId}/primary")
    @PreAuthorize("hasRole('ADMIN')")
    public void markImage(@PathVariable Long productId, @PathVariable Long imageId) {
        productService.markDefaultImage(productId, imageId);
    }


    @DeleteMapping("/{productId}/images/{imageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteImage(@PathVariable Long productId, @PathVariable Long imageId) {
        productService.deleteImage(productId, imageId);
    }

    @GetMapping()
    public Page<ProductResponse> searchProducts(@RequestParam(required = false) String keyword,
                                                @RequestParam(required = false) Long categoryId,
                                                @RequestParam(required = false) String brand,
                                                @RequestParam(required = false) BigDecimal minPrice,
                                                @RequestParam(required = false) BigDecimal maxPrice,
                                                @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable  pageable) {
        return productService.searchProducts(keyword, categoryId, brand, minPrice, maxPrice, pageable);
    }
}
