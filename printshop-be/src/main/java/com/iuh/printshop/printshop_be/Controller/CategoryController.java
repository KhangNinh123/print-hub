package com.iuh.printshop.printshop_be.Controller;

import com.iuh.printshop.printshop_be.Service.CategoryService;
import com.iuh.printshop.printshop_be.model.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*") // Cho phép frontend gọi API từ domain khác
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // Lấy tất cả categories
    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.findAll();
        return ResponseEntity.ok(categories);
    }

    // Lấy category theo ID
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Integer id) {
        Category category = categoryService.findById(id);
        return (category != null)
                ? ResponseEntity.ok(category)
                : ResponseEntity.notFound().build();
    }

    // Tìm category theo tên
    @GetMapping("/search")
    public ResponseEntity<Category> getCategoryByName(@RequestParam String name) {
        Category category = categoryService.findByName(name);
        return (category != null)
                ? ResponseEntity.ok(category)
                : ResponseEntity.notFound().build();
    }

    // Thêm mới hoặc cập nhật category
    @PostMapping
    public ResponseEntity<Category> createOrUpdateCategory(@RequestBody Category category) {
        Category savedCategory = categoryService.save(category);
        return ResponseEntity.ok(savedCategory);
    }

    // Xóa category theo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Integer id) {
        Category category = categoryService.findById(id);
        if (category != null) {
            categoryService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
