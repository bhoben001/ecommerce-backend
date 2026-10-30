package com.ecommerce.service;

import com.ecommerce.dto.request.ProductCreateRequest;
import com.ecommerce.dto.request.ProductImageRequest;
import com.ecommerce.dto.request.ProductUpdateRequest;
import com.ecommerce.dto.request.StockUpdateRequest;
import com.ecommerce.dto.response.ProductResponse;
import com.ecommerce.entity.Category;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.ProductImage;
import com.ecommerce.exception.BadRequestException;
import com.ecommerce.exception.ConflictException;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.mapper.ProductMapper;
import com.ecommerce.repository.CategoryRepository;
import com.ecommerce.repository.ProductImageRepository;
import com.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor()
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    @Transactional
    public ProductResponse createProduct(ProductCreateRequest product) {

        if (productRepository.existsBySku(product.getSku())) throw new ConflictException("Product SKU already exists");

        Category category = categoryRepository.findById(product.getCategoryId()).orElseThrow(() ->
                new ResourceNotFoundException("Category not found with id " + product.getCategoryId()));

        BigDecimal discount=product.getDiscountPrice();
        BigDecimal price=product.getPrice();

        if (discount != null && discount.compareTo(price) >= 0)
            throw new BadRequestException("discountPrice must be less than price");

        Product newProduct=new Product();
        newProduct.setName(product.getName());
        newProduct.setDescription(product.getDescription());
        newProduct.setPrice(product.getPrice());
        newProduct.setDiscountPrice(product.getDiscountPrice());
        newProduct.setSku(product.getSku());
        newProduct.setStockQuantity(product.getStockQuantity());
        newProduct.setBrand(product.getBrand());
        newProduct.setThumbnailUrl(product.getThumbnailUrl());
        newProduct.setCategory(category);

        Product saved=productRepository.save(newProduct);
        return productMapper.toResponse(saved,true);
    }


    @Transactional
    public ProductResponse updateProduct(Long id, ProductUpdateRequest request){
        Product product = productRepository.findById(id)
                .filter(p->Boolean.TRUE.equals(p.getActive()))
                .orElseThrow(()->new ResourceNotFoundException("Product not found with id " + id));

        if(request.getName()!=null) product.setName(request.getName());
        if(request.getDescription()!=null) product.setDescription(request.getDescription());
        if(request.getPrice()!=null) product.setPrice(request.getPrice());
        if(request.getDiscountPrice()!=null) product.setDiscountPrice(request.getDiscountPrice());
        if(request.getStockQuantity()!=null) product.setStockQuantity(request.getStockQuantity());
        if(request.getBrand()!=null) product.setBrand(request.getBrand());
        if(request.getThumbnailUrl()!=null) product.setThumbnailUrl(request.getThumbnailUrl());

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow(() ->
                            new ResourceNotFoundException("Category not found with id " + request.getCategoryId()));
            product.setCategory(category);
        }

        if (product.getDiscountPrice() != null && product.getDiscountPrice().compareTo(product.getPrice()) >= 0)
            throw new BadRequestException("discountPrice must be less than price");

//        sku validation
        if (request.getSku() != null && !request.getSku().equals(product.getSku())
                && productRepository.existsBySkuAndIdNot(request.getSku(), id)) {
            throw new ConflictException("Product SKU already exists");
        }
        if (request.getSku() != null) product.setSku(request.getSku());


        return productMapper.toResponse(productRepository.save(product), true);
    }


    @Transactional
    public void deactivateProduct(Long id){
        Product product = productRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException("Product not found with id " + id));
        product.setActive(false);
    }

    @Transactional
    public void activateProduct(Long id) {
        Product product = productRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException("Product not found with id " + id));
        product.setActive(true);
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id){
        Product product=productRepository.findById(id)
                .filter(x -> Boolean.TRUE.equals(x.getActive()))
                .orElseThrow(()-> new ResourceNotFoundException("Product not found with id " + id));

        return productMapper.toResponse(product,true);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        Page<Product> products= productRepository.findAllByActiveTrue(pageable);
        return products.map(product->productMapper.toResponse(product,true));
    }

    @Transactional
    public ProductResponse updateStock(Long id, StockUpdateRequest request) {
        Product product = productRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException("Product not found with id " + id));
        Integer quantity = request.getStockQuantity();

        if (quantity == null || quantity <= 0)
            throw new IllegalArgumentException("Stock quantity must be greater than zero");

        product.setStockQuantity(quantity+product.getStockQuantity());
        return productMapper.toResponse(product, true);
    }

    @Transactional
    public ProductResponse addImage(Long id, ProductImageRequest request) {
        Product product = productRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException("Product not found with id " + id));

        ProductImage image=new ProductImage();
        image.setProduct(product);
        image.setImageUrl(request.getImageUrl());
        image.setDisplayOrder(request.getDisplayOrder());

        productImageRepository.save(image);

        product.getImages().add(image);
        return productMapper.toResponse(product, true);
    }

    @Transactional
    public void markDefaultImage(Long productId,Long imageId) {

        List<ProductImage> images = productImageRepository.findByProductId(productId);

        boolean exists = images.stream().anyMatch(img -> img.getId().equals(imageId));
        if (!exists) throw new ResourceNotFoundException("Image not found with id " + imageId);

        for (ProductImage image : images) {
            image.setIsPrimary(image.getId().equals(imageId));
        }
    }

    @Transactional
    public void deleteImage(Long productId,Long imageId) {

        Product product=productRepository.findById(productId).orElseThrow(()->
                new ResourceNotFoundException("Product not found with id " + productId));

        ProductImage image=productImageRepository.findById(imageId)
                .filter(i -> i.getProduct().getId().equals(productId)).orElseThrow(()->
                new ResourceNotFoundException("Image not found with id " + imageId));

        product.getImages().remove(image);
    }


    @Transactional(readOnly = true)
    public Page<ProductResponse> searchProducts(String keyword, Long categoryId, String brand,
                                                BigDecimal minPrice, BigDecimal maxPrice,
                                                Pageable pageable) {
        if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0)
            throw new BadRequestException("minPrice cannot be greater than maxPrice");

        String pattern = (keyword == null || keyword.isBlank()) ? null : "%" + keyword.trim().toLowerCase() + "%";
        String brandName = (brand == null || brand.isBlank()) ? null : brand.trim().toLowerCase();

        return productRepository.search(pattern, categoryId, brandName, minPrice, maxPrice, pageable)
                .map(p -> productMapper.toResponse(p, false));
    }


}
