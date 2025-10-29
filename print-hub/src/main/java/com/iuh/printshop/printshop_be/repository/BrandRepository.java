package com.iuh.printshop.printshop_be.repository;

import com.iuh.printshop.printshop_be.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand, Integer> {
    Optional<Brand> findByName(String name);
}

