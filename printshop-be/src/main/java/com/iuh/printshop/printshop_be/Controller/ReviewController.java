package com.iuh.printshop.printshop_be.Controller;

import com.iuh.printshop.printshop_be.Service.AuthService;
import com.iuh.printshop.printshop_be.Service.ReviewService;
import com.iuh.printshop.printshop_be.model.Review;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api") @RequiredArgsConstructor
public class ReviewController {
    private final ReviewService service;
    private final AuthService auth;

    public record ReviewReq(@NotNull Integer productId, @Min(1) @Max(5) int rating,
                            String title, String content) {}

    @GetMapping("/products/{pid}/reviews")
    public Page<Review> list(@PathVariable Integer pid,
                             @RequestParam(defaultValue="0") int page,
                             @RequestParam(defaultValue="10") int size) {
        return service.listByProduct(pid, page, size);
    }

    @GetMapping("/products/{pid}/rating")
    public Object summary(@PathVariable Integer pid) {
        return service.summary(pid);
    }

    @PostMapping("/products/{pid}/reviews")
    public ResponseEntity<?> create(@PathVariable Integer pid, @RequestBody ReviewReq req) {
        var r = service.create(auth.currentUserId(), pid, req.rating(), req.title(), req.content());
        return ResponseEntity.ok(r);
    }

    @PutMapping("/reviews/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ReviewReq req) {
        var r = service.update(id, auth.currentUserId(), req.rating(), req.title(), req.content(), auth.isAdmin());
        return ResponseEntity.ok(r);
    }

    @DeleteMapping("/reviews/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.delete(id, auth.currentUserId(), auth.isAdmin());
        return ResponseEntity.noContent().build();
    }
}
