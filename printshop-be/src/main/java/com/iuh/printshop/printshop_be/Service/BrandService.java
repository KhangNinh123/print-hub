package com.iuh.printshop.printshop_be.Service;

import com.iuh.printshop.printshop_be.Repository.BrandRepository;
import com.iuh.printshop.printshop_be.model.Brand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BrandService {

    @Autowired
    private BrandRepository repo;

    public List<Brand> findAll() {
        return repo.findAll();
    }


    public Brand findById(Integer id) {
        Optional<Brand> brand = repo.findById(id);
        return brand.orElse(null);
    }


    public Brand save(Brand brand) {
        return repo.save(brand);
    }


    public void deleteById(Integer id) {
        repo.deleteById(id);
    }


    public Brand findByName(String name) {
        return repo.findByName(name);
    }

}
