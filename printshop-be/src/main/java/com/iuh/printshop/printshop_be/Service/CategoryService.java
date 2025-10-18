package com.iuh.printshop.printshop_be.Service;

import com.iuh.printshop.printshop_be.Repository.CategoryRepository;
import com.iuh.printshop.printshop_be.model.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository repo;


    public List<Category> findAll() {
        return repo.findAll();
    }


    public Category findById(Integer id) {
        Optional<Category> category = repo.findById(id);
        return category.orElse(null);
    }


    public Category save(Category category) {
        return repo.save(category);
    }


    public void deleteById(Integer id) {
        repo.deleteById(id);
    }


    public Category findByName(String name) {
        return repo.findByName(name);
    }
}
