package com.iuh.printshop.printshop_be.service;

import com.iuh.printshop.printshop_be.entity.Brand;
import com.iuh.printshop.printshop_be.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BrandService {
    private final BrandRepository brandRepository;

    public Brand createBrand(Brand brand) {
        return brandRepository.save(brand);
    }

    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }

    public Optional<Brand> getBrandById(Integer id) {
        return brandRepository.findById(id);
    }

    public Optional<Brand> updateBrand(Integer id, Brand updatedBrand) {
        return brandRepository.findById(id).map(brand -> {
            brand.setName(updatedBrand.getName());
            brand.setDescription(updatedBrand.getDescription());
            return brandRepository.save(brand);
        });
    }

    public boolean deleteBrand(Integer id) {
        if (brandRepository.existsById(id)) {
            brandRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Optional<Brand> getBrandByName(String name) {
        return brandRepository.findByName(name);
    }
}

