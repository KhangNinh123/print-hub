package com.iuh.printshop.printshop_be.Controller;

import com.iuh.printshop.printshop_be.Service.ProductService;
import com.iuh.printshop.printshop_be.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*") // Cho phép frontend truy cập từ domain khác
public class ProductController {

    @Autowired
    private ProductService productService;

    //Lấy tất cả sản phẩm
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAll();
        return ResponseEntity.ok(products);
    }

    //Lấy sản phẩm theo ID
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Integer id) {
        Product product = productService.getById(id);
        return (product != null)
                ? ResponseEntity.ok(product)
                : ResponseEntity.notFound().build();
    }

    // Tìm sản phẩm theo categoryId
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable Integer categoryId) {
        List<Product> products = productService.findByCategoryId(categoryId);
        return ResponseEntity.ok(products);
    }

    // Tìm sản phẩm theo brandId
    @GetMapping("/brand/{brandId}")
    public ResponseEntity<List<Product>> getProductsByBrand(@PathVariable Integer brandId) {
        List<Product> products = productService.findByBrandId(brandId);
        return ResponseEntity.ok(products);
    }

    // Tìm sản phẩm theo tên (chứa chuỗi, không phân biệt hoa thường)
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProductsByName(@RequestParam String name) {
        List<Product> products = productService.findByNameContainingIgnoreCase(name);
        return ResponseEntity.ok(products);
    }

    // Thêm sản phẩm mới
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product createdProduct = productService.create(product);
        return ResponseEntity.ok(createdProduct);
    }

    // Cập nhật sản phẩm
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Integer id, @RequestBody Product product) {
        Product existingProduct = productService.getById(id);
        if (existingProduct == null) {
            return ResponseEntity.notFound().build();
        }
        product.setId(id); // đảm bảo đúng ID khi cập nhật
        Product updatedProduct = productService.update(product);
        return ResponseEntity.ok(updatedProduct);
    }

    // Xóa sản phẩm theo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Integer id) {
        Product existingProduct = productService.getById(id);
        if (existingProduct != null) {
            productService.delete(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
