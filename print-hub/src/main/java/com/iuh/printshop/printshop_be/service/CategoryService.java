package com.iuh.printshop.printshop_be.service;

import com.iuh.printshop.printshop_be.dto.CategoryRequest;
import com.iuh.printshop.printshop_be.entity.Category;
import com.iuh.printshop.printshop_be.repository.CategoryRepository;
import lombok.RequiredArgsConstructor; // Sử dụng Lombok để đơn giản hóa Constructor
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional; // Cần thiết

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // 1. READ ALL (Public)
    public List<Category> findAllCategories() {
        return categoryRepository.findAll();
    }

    // 2. READ ONE (Public) - Trả về Optional
    public Optional<Category> findCategoryById(Integer id) {
        return categoryRepository.findById(id);
    }

    // 3. CREATE (Admin ONLY)
    @Transactional
    public Optional<Category> createCategory(CategoryRequest request) {
        // Kiểm tra tên trùng lặp (Logic bảo toàn dữ liệu)
        if (categoryRepository.findByName(request.getName()).isPresent()) {
            return Optional.empty();
        }

        Category newCategory = new Category(request.getName(), request.getDescription());
        return Optional.of(categoryRepository.save(newCategory));
    }

    // 4. UPDATE (Admin ONLY) - Trả về Optional
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

    // 5. DELETE (Admin ONLY) - Trả về boolean
    public boolean deleteCategory(Integer id) {
        if (categoryRepository.existsById(id)) {
            categoryRepository.deleteById(id);
            return true;
        }
        return false;
    }
}