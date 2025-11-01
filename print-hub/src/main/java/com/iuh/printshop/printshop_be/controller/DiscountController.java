package com.iuh.printshop.printshop_be.controller;

import com.iuh.printshop.printshop_be.dto.discount.ApplyDiscountRequest;
import com.iuh.printshop.printshop_be.dto.discount.DiscountCalculationResult;
import com.iuh.printshop.printshop_be.dto.discount.DiscountCodeRequest;
import com.iuh.printshop.printshop_be.dto.discount.DiscountCodeResponse;
import com.iuh.printshop.printshop_be.service.DiscountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/discounts")
@RequiredArgsConstructor
public class DiscountController {
    private final DiscountService discountService;

    @PostMapping
    public ResponseEntity<DiscountCodeResponse> createDiscountCode(@Valid @RequestBody DiscountCodeRequest request) {
        DiscountCodeResponse response = discountService.createDiscountCode(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<DiscountCodeResponse>> getAllDiscountCodes() {
        List<DiscountCodeResponse> responses = discountService.getAllDiscountCodes();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DiscountCodeResponse> getDiscountCodeById(@PathVariable Integer id) {
        return discountService.getDiscountCodeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<DiscountCodeResponse> getDiscountCodeByCode(@PathVariable String code) {
        return discountService.getDiscountCodeByCode(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<DiscountCodeResponse> updateDiscountCode(@PathVariable Integer id, @Valid @RequestBody DiscountCodeRequest request) {
        return discountService.updateDiscountCode(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiscountCode(@PathVariable Integer id) {
        if (discountService.deleteDiscountCode(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/apply")
    public ResponseEntity<DiscountCalculationResult> applyDiscountCode(@Valid @RequestBody ApplyDiscountRequest request, @RequestParam BigDecimal orderTotal) {
        try {
            DiscountCalculationResult result = discountService.applyDiscountCode(request.getCode(), orderTotal);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<Boolean> validateDiscountCode(@Valid @RequestBody ApplyDiscountRequest request, @RequestParam BigDecimal orderTotal) {
        boolean isValid = discountService.validateDiscountCode(request.getCode(), orderTotal);
        return ResponseEntity.ok(isValid);
    }
}
