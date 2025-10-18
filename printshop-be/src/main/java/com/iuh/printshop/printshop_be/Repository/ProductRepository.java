package com.iuh.printshop.printshop_be.Repository;

import com.iuh.printshop.printshop_be.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findByCategoryId(Integer categoryId);
    List<Product> findByBrandId(Integer brandId);
    List<Product> findByNameContainingIgnoreCase(String name);
}
