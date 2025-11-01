package com.iuh.printshop.printshop_be.controller;

import com.iuh.printshop.printshop_be.dto.discount.DiscountCalculationResult;
import com.iuh.printshop.printshop_be.dto.promotion.PromotionRequest;
import com.iuh.printshop.printshop_be.dto.promotion.PromotionResponse;
import com.iuh.printshop.printshop_be.service.PromotionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/promotions")
@RequiredArgsConstructor
public class PromotionController {
    private final PromotionService promotionService;

    @PostMapping
    public ResponseEntity<PromotionResponse> createPromotion(@Valid @RequestBody PromotionRequest request) {
        PromotionResponse response = promotionService.createPromotion(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<PromotionResponse>> getAllPromotions() {
        List<PromotionResponse> responses = promotionService.getAllPromotions();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/active")
    public ResponseEntity<List<PromotionResponse>> getActivePromotions() {
        List<PromotionResponse> responses = promotionService.getActivePromotions();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PromotionResponse> getPromotionById(@PathVariable Integer id) {
        return promotionService.getPromotionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<PromotionResponse> updatePromotion(@PathVariable Integer id, @Valid @RequestBody PromotionRequest request) {
        return promotionService.updatePromotion(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePromotion(@PathVariable Integer id) {
        if (promotionService.deletePromotion(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/calculate")
    public ResponseEntity<DiscountCalculationResult> calculatePromotionDiscount(
            @RequestParam BigDecimal orderTotal,
            @RequestParam(required = false) List<Integer> productIds,
            @RequestParam(required = false) List<Integer> categoryIds) {
        DiscountCalculationResult result = promotionService.calculatePromotionDiscount(orderTotal, productIds, categoryIds);
        return ResponseEntity.ok(result);
    }
}
