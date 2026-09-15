package com.ecommerce.productservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(nullable = false)
    private Double price;

    private Double cost;

    @Column(name = "stock_quantity")
    private Integer stockQuantity = 0;

    @Column(name = "reorder_level")
    private Integer reorderLevel = 10;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_date")
    private LocalDateTime createdDate = LocalDateTime.now();

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated = LocalDateTime.now();
}