package com.iuh.printshop.printshop_be.controller;

import com.iuh.printshop.printshop_be.dto.cart.AddToCartRequest;
import com.iuh.printshop.printshop_be.dto.cart.CartResponse;
import com.iuh.printshop.printshop_be.dto.cart.UpdateCartItemRequest;
import com.iuh.printshop.printshop_be.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
// @PreAuthorize("hasRole('CUSTOMER')") // Chỉ CUSTOMER mới dùng giỏ hàng - Tạm thời comment để test
public class CartController {

    private final CartService cartService;

    @GetMapping("/{userId}")
    public ResponseEntity<CartResponse> getCart(@PathVariable Integer userId) {
        return ResponseEntity.ok(cartService.getCart(userId));
    }

    @PostMapping("/{userId}/items")
    public ResponseEntity<CartResponse> addItem(@PathVariable Integer userId, 
                                                @Valid @RequestBody AddToCartRequest req) {
        return ResponseEntity.ok(cartService.addItem(userId, req));
    }

    @PutMapping("/{userId}/items/{itemId}")
    public ResponseEntity<CartResponse> updateItem(@PathVariable Integer userId, 
                                                    @PathVariable Integer itemId,
                                                   @Valid @RequestBody UpdateCartItemRequest req) {
        return ResponseEntity.ok(cartService.updateItem(userId, itemId, req));
    }

    @DeleteMapping("/{userId}/items/{itemId}")
    public ResponseEntity<CartResponse> removeItem(@PathVariable Integer userId, 
                                                   @PathVariable Integer itemId) {
        return ResponseEntity.ok(cartService.removeItem(userId, itemId));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<CartResponse> clear(@PathVariable Integer userId) {
        return ResponseEntity.ok(cartService.clear(userId));
    }
}
