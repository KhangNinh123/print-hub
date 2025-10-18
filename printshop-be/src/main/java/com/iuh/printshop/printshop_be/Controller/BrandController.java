package com.iuh.printshop.printshop_be.Controller;

import com.iuh.printshop.printshop_be.Service.BrandService;
import com.iuh.printshop.printshop_be.model.Brand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brands")
@CrossOrigin(origins = "*")
public class BrandController {
    @Autowired
    private BrandService brandService;

    // Lấy danh sách tất cả Brand
    @GetMapping
    public ResponseEntity<List<Brand>> getAllBrands() {
        List<Brand> brands = brandService.findAll();
        return ResponseEntity.ok(brands);
    }

    // Lấy Brand theo ID
    @GetMapping("/{id}")
    public ResponseEntity<Brand> getBrandById(@PathVariable Integer id) {
        Brand brand = brandService.findById(id);
        return (brand != null)
                ? ResponseEntity.ok(brand)
                : ResponseEntity.notFound().build();
    }

    // Tìm Brand theo tên
    @GetMapping("/search")
    public ResponseEntity<Brand> getBrandByName(@RequestParam String name) {
        Brand brand = brandService.findByName(name);
        return (brand != null)
                ? ResponseEntity.ok(brand)
                : ResponseEntity.notFound().build();
    }

    // Thêm mới hoặc cập nhật Brand
    @PostMapping
    public ResponseEntity<Brand> createOrUpdateBrand(@RequestBody Brand brand) {
        Brand savedBrand = brandService.save(brand);
        return ResponseEntity.ok(savedBrand);
    }

    // Xoá Brand theo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBrand(@PathVariable Integer id) {
        Brand brand = brandService.findById(id);
        if (brand != null) {
            brandService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
