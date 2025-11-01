package com.iuh.printshop.printshop_be.repository;

import com.iuh.printshop.printshop_be.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    boolean existsByName(String name);

    Page<Product> findAll(Pageable pageable);

    @Query("""
    select p from Product p
    where(:keyword is null or lower(p.name) like lower(concat('%', :keyword, '%') ) )
    and (:categoryId is null or p.category.id = :categoryId) 
    and (:brandId is null or p.brand.id = :brandId) 
    and (:minPrice is null or p.price >= :minPrice) 
    and (:maxPrice is null or p.price <= :maxPrice) 
    and (:inStock is null  or 
    ((:inStock = TRUE AND p.stockQuantity > 0) OR (:inStock = FALSE AND p.stockQuantity = 0)))
""")
    Page<Product> filterProducts (
            @Param("keyword") String keyword,
            @Param("categoryId") Integer categoryId,
            @Param("brandId") Integer brandId,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("inStock") Boolean inStock,
            Pageable pageable
    );
}

