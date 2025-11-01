package com.iuh.printshop.printshop_be.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
    @NotBlank(message = "Product name không được trống")
    private String name;

    private String description;

    @NotNull(message = "Price không được trống")
    @Positive(message = "Price phải dương")
    private BigDecimal price;

    @NotNull(message = "Stock quantity không được trống")
    @Positive(message = "Stock quantity phải dương hoặc bằng 0")
    private Integer stockQuantity;

    private String imageUrl;

    @NotNull(message = "Category ID không được trống")
    private Integer categoryId;

    @NotNull(message = "Brand ID không được trống")
    private Integer brandId;
}

