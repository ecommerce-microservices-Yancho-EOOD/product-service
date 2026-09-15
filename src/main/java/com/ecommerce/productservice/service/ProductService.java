package com.ecommerce.productservice.service;

import com.ecommerce.productservice.dto.ProductRequest;
import com.ecommerce.productservice.dto.ProductResponse;
import com.ecommerce.productservice.entity.Category;
import com.ecommerce.productservice.entity.Product;
import com.ecommerce.productservice.exception.BusinessException;
import com.ecommerce.productservice.exception.ResourceNotFoundException;
import com.ecommerce.productservice.repository.CategoryRepository;
import com.ecommerce.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    // ============ CREATE ============
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        log.info("Creating product: {}", request.getName());

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .category(category)
                .price(request.getPrice())
                .cost(request.getCost())
                .stockQuantity(request.getStockQuantity() != null ? request.getStockQuantity() : 0)
                .reorderLevel(request.getReorderLevel() != null ? request.getReorderLevel() : 10)
                .isActive(true)
                .createdDate(LocalDateTime.now())
                .lastUpdated(LocalDateTime.now())
                .build();

        Product saved = productRepository.save(product);
        log.info("Product created with ID: {}", saved.getId());
        return toResponse(saved);
    }

    // ============ READ ============
    public ProductResponse getProduct(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        return toResponse(product);
    }

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ProductResponse> getActiveProducts() {
        return productRepository.findByIsActiveTrue().stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ProductResponse> getProductsByCategory(UUID categoryId) {
        return productRepository.findByCategoryId(categoryId).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ProductResponse> searchProducts(String keyword) {
        return productRepository.searchProducts(keyword).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ProductResponse> getProductsByPriceRange(Double min, Double max) {
        return productRepository.findByPriceRange(min, max).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ProductResponse> getProductsNeedingRestock() {
        return productRepository.findProductsNeedingRestock().stream()
                .map(this::toResponse)
                .toList();
    }

    // ============ UPDATE ============
    @Transactional
    public ProductResponse updateProduct(UUID id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        if (request.getName() != null) product.setName(request.getName());
        if (request.getDescription() != null) product.setDescription(request.getDescription());
        if (request.getPrice() != null) product.setPrice(request.getPrice());
        if (request.getCost() != null) product.setCost(request.getCost());
        if (request.getStockQuantity() != null) product.setStockQuantity(request.getStockQuantity());
        if (request.getReorderLevel() != null) product.setReorderLevel(request.getReorderLevel());

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));
            product.setCategory(category);
        }

        product.setLastUpdated(LocalDateTime.now());
        return toResponse(productRepository.save(product));
    }

    @Transactional
    public ProductResponse updateStock(UUID id, Integer quantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        if (quantity < 0) {
            throw new BusinessException("Quantity cannot be negative");
        }

        product.setStockQuantity(quantity);
        product.setLastUpdated(LocalDateTime.now());
        return toResponse(productRepository.save(product));
    }

    // ============ DELETE ============
    @Transactional
    public void deleteProduct(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        product.setIsActive(false);
        product.setLastUpdated(LocalDateTime.now());
        productRepository.save(product);
        log.info("Product soft deleted: {}", id);
    }

    // ============ MAPPING ============
    private ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .price(product.getPrice())
                .cost(product.getCost())
                .stockQuantity(product.getStockQuantity())
                .reorderLevel(product.getReorderLevel())
                .isActive(product.getIsActive())
                .createdDate(product.getCreatedDate())
                .build();
    }
}