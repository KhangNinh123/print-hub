package com.iuh.printshop.printshop_be.Service;

import com.iuh.printshop.printshop_be.Repository.ProductRepository;
import com.iuh.printshop.printshop_be.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    @Autowired
    private ProductRepository repo;


    public List<Product> getAll() {
        return repo.findAll();
    }


    public Product getById(Integer id) {
        return repo.findById(id).orElse(null);
    }


    public Product create(Product product) {
        return repo.save(product);
    }


    public Product update(Product product) {
        return repo.save(product);
    }


    public void delete(Integer id) {
        repo.deleteById(id);
    }

    public List<Product> findByCategoryId(Integer categoryId) {
        return repo.findByCategoryId(categoryId);
    }


    public List<Product> findByBrandId(Integer brandId) {
        return repo.findByBrandId(brandId);
    }


    public List<Product> findByNameContainingIgnoreCase(String name) {
        return repo.findByNameContainingIgnoreCase(name);
    }
}
