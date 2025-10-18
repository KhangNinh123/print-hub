package com.iuh.printshop.printshop_be.Repository;

import com.iuh.printshop.printshop_be.model.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BrandRepository extends JpaRepository<Brand,Integer> {
    Brand findByName(String name);
}
