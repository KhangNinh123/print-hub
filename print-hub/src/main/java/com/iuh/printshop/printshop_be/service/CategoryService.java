package com.iuh.printshop.printshop_be.service;

import com.iuh.printshop.printshop_be.dto.category.CategoryRequest;
import com.iuh.printshop.printshop_be.entity.Category;
import com.iuh.printshop.printshop_be.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    // 1. READ ALL (Public)
    public List<Category> findAllCategories() {
        return categoryRepository.findAll();
    }

    // 2. READ ONE (Public)
    public Optional<Category> findCategoryById(Integer id) {
        return categoryRepository.findById(id);
    }

    // 3. CREATE (Admin)
    @Transactional
    public Optional<Category> createCategory(CategoryRequest request) {
        // Kiểm tra tên trùng lặp (Logic bảo toàn dữ liệu)
        if (categoryRepository.findByName(request.getName()).isPresent()) {
            return Optional.empty();
        }

        Category newCategory = new Category(request.getName(), request.getDescription());
        return Optional.of(categoryRepository.save(newCategory));
    }

    // 4. UPDATE (Admin)
    @Transactional
    public Optional<Category> updateCategory(Integer id, CategoryRequest request) {
        return categoryRepository.findById(id).map(existingCategory -> {

            // Kiểm tra trùng lặp (trừ chính nó)
            if (categoryRepository.existsByNameAndIdNot(request.getName(), id)) {
                existingCategory.setName(request.getName());
                existingCategory.setDescription(request.getDescription());
                return categoryRepository.save(existingCategory);
            }

            existingCategory.setName(request.getName());
            existingCategory.setDescription(request.getDescription());
            return categoryRepository.save(existingCategory);
        });
    }

    // 5. DELETE (Admin)
    public boolean deleteCategory(Integer id) {
        if (categoryRepository.existsById(id)) {
            categoryRepository.deleteById(id);
            return true;
        }
        return false;
    }
}