package com.iuh.printshop.printshop_be.repository;

import com.iuh.printshop.printshop_be.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {
}

