package com.iuh.printshop.printshop_be.repository;

import com.iuh.printshop.printshop_be.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    
    // Find products by category
    Page<Product> findByCategoryId(Integer categoryId, Pageable pageable);
    
    // Find products by brand
    Page<Product> findByBrandId(Integer brandId, Pageable pageable);
    
    // Search products by name
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
    
    // Find products by price range
    Page<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    
    // Find products with stock
    Page<Product> findByStockQuantityGreaterThan(Integer quantity, Pageable pageable);
    
    // Complex search query
    @Query("SELECT p FROM Product p WHERE " +
           "(:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:categoryId IS NULL OR p.category.id = :categoryId) AND " +
           "(:brandId IS NULL OR p.brand.id = :brandId) AND " +
           "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.price <= :maxPrice)")
    Page<Product> searchProducts(
        @Param("name") String name,
        @Param("categoryId") Integer categoryId,
        @Param("brandId") Integer brandId,
        @Param("minPrice") BigDecimal minPrice,
        @Param("maxPrice") BigDecimal maxPrice,
        Pageable pageable
    );
}

