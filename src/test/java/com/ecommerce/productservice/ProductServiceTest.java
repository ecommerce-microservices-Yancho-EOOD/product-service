package com.ecommerce.productservice;

import com.ecommerce.productservice.dto.ProductRequest;
import com.ecommerce.productservice.dto.ProductResponse;
import com.ecommerce.productservice.entity.Category;
import com.ecommerce.productservice.entity.Product;
import com.ecommerce.productservice.exception.BusinessException;
import com.ecommerce.productservice.exception.ResourceNotFoundException;
import com.ecommerce.productservice.repository.CategoryRepository;
import com.ecommerce.productservice.repository.ProductRepository;
import com.ecommerce.productservice.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

    private UUID productId;
    private UUID categoryId;
    private Product product;
    private Category category;
    private ProductRequest productRequest;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();
        categoryId = UUID.randomUUID();

        category = Category.builder()
                .id(categoryId)
                .name("Electronics")
                .description("Electronic devices")
                .build();

        product = Product.builder()
                .id(productId)
                .name("Laptop")
                .description("Gaming laptop")
                .category(category)
                .price(999.99)
                .cost(700.00)
                .stockQuantity(10)
                .reorderLevel(5)
                .isActive(true)
                .createdDate(LocalDateTime.now())
                .lastUpdated(LocalDateTime.now())
                .build();

        productRequest = ProductRequest.builder()
                .name("Laptop")
                .description("Gaming laptop")
                .categoryId(categoryId)
                .price(999.99)
                .cost(700.00)
                .stockQuantity(10)
                .reorderLevel(5)
                .build();
    }

    @Test
    @DisplayName("Should create product successfully")
    void createProduct_ShouldSucceed() {
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductResponse response = productService.createProduct(productRequest);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Laptop");
        assertThat(response.getPrice()).isEqualTo(999.99);
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw exception when category not found")
    void createProduct_ShouldThrowException_WhenCategoryNotFound() {
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.createProduct(productRequest))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should return product when exists")
    void getProduct_ShouldReturnProduct() {
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        ProductResponse response = productService.getProduct(productId);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(productId);
    }

    @Test
    @DisplayName("Should throw exception when product not found")
    void getProduct_ShouldThrowException_WhenNotFound() {
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProduct(productId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should return all products")
    void getAllProducts_ShouldReturnList() {
        when(productRepository.findAll()).thenReturn(List.of(product));

        List<ProductResponse> responses = productService.getAllProducts();

        assertThat(responses).hasSize(1);
    }

    @Test
    @DisplayName("Should update product successfully")
    void updateProduct_ShouldSucceed() {
        ProductRequest updateRequest = ProductRequest.builder()
                .name("Gaming Laptop")
                .price(1199.99)
                .build();

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        productService.updateProduct(productId, updateRequest);

        assertThat(product.getName()).isEqualTo("Gaming Laptop");
        assertThat(product.getPrice()).isEqualTo(1199.99);
    }

    @Test
    @DisplayName("Should update stock successfully")
    void updateStock_ShouldSucceed() {
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductResponse response = productService.updateStock(productId, 25);

        assertThat(response.getStockQuantity()).isEqualTo(25);
    }

    @Test
    @DisplayName("Should throw exception when stock is negative")
    void updateStock_ShouldThrowException_WhenNegative() {
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> productService.updateStock(productId, -5))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("Should soft delete product successfully")
    void deleteProduct_ShouldSoftDelete() {
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        productService.deleteProduct(productId);

        assertThat(product.getIsActive()).isFalse();
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent product")
    void deleteProduct_ShouldThrowException_WhenNotFound() {
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.deleteProduct(productId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should search products by keyword")
    void searchProducts_ShouldReturnMatching() {
        when(productRepository.searchProducts("Laptop")).thenReturn(List.of(product));

        List<ProductResponse> responses = productService.searchProducts("Laptop");

        assertThat(responses).hasSize(1);
    }

    @Test
    @DisplayName("Should return products needing restock")
    void getProductsNeedingRestock_ShouldReturnList() {
        when(productRepository.findProductsNeedingRestock()).thenReturn(List.of(product));

        List<ProductResponse> responses = productService.getProductsNeedingRestock();

        assertThat(responses).hasSize(1);
    }
}