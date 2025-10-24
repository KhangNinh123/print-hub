package com.iuh.printshop.printshop_be.Controller;

import com.iuh.printshop.printshop_be.Service.AuthService;
import com.iuh.printshop.printshop_be.Service.FeedbackService;
import com.iuh.printshop.printshop_be.model.Feedback;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api") @RequiredArgsConstructor
public class FeedbackController {
    private final FeedbackService service;
    private final AuthService auth;

    public record FeedbackReq(@NotBlank String type, String subject,
                              @NotBlank String content, Integer rating, Long orderId) {}

    @PostMapping("/feedbacks")
    public ResponseEntity<?> create(@RequestBody FeedbackReq req) {
        Feedback fb = service.create(auth.currentUserId(), req.type(), req.subject(),
                req.content(), req.rating(), req.orderId());
        return ResponseEntity.ok(fb);
    }

    // Người dùng xem feedback của chính mình
    @GetMapping("/me/feedbacks")
    public Page<Feedback> myFeedbacks(@RequestParam(defaultValue="0") int page,
                                      @RequestParam(defaultValue="10") int size) {
        return service.listMine(auth.currentUserId(), page, size);
    }

    // Admin xem tất cả
    @GetMapping("/feedbacks")
    public Page<Feedback> all(@RequestParam(defaultValue="0") int page,
                              @RequestParam(defaultValue="10") int size) {
        if (!auth.isAdmin()) throw new SecurityException("Chỉ admin xem tất cả feedback");
        return service.listAll(page, size);
    }

    @GetMapping("/orders/{orderId}/feedbacks")
    public Page<Feedback> byOrder(@PathVariable Long orderId,
                                  @RequestParam(defaultValue="0") int page,
                                  @RequestParam(defaultValue="10") int size) {
        return service.listByOrder(orderId, page, size);
    }
}
