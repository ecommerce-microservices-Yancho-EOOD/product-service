package com.ecommerce.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private UUID id;
    private String name;
    private String description;
    private UUID categoryId;
    private String categoryName;
    private Double price;
    private Double cost;
    private Integer stockQuantity;
    private Integer reorderLevel;
    private Boolean isActive;
    private LocalDateTime createdDate;
}