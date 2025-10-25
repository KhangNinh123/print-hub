package com.iuh.printshop.printshop_be.service.impl;

import com.iuh.printshop.printshop_be.dto.cart.AddToCartRequest;
import com.iuh.printshop.printshop_be.dto.cart.CartItemResponse;
import com.iuh.printshop.printshop_be.dto.cart.CartResponse;
import com.iuh.printshop.printshop_be.dto.cart.UpdateCartItemRequest;
import com.iuh.printshop.printshop_be.entity.*;
import com.iuh.printshop.printshop_be.repository.*;
import com.iuh.printshop.printshop_be.service.CartService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    // Lấy user từ SecurityContext (username = email)
    private User currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + email));
    }

    private Cart getOrCreateCart(User user) {
        return cartRepository.findByUser(user).orElseGet(() -> {
            Cart c = Cart.builder().user(user).total(BigDecimal.ZERO).build();
            return cartRepository.save(c);
        });
    }

    private CartResponse toResponse(Cart cart) {
        List<CartItemResponse> items = cart.getItems().stream().map(ci -> {
            // Đảm bảo product được load để tránh LazyInitializationException
            Product product = ci.getProduct();
            return CartItemResponse.builder()
                    .itemId(ci.getId())
                    .productId(product.getId())
                    .productName(product.getName())
                    .unitPrice(ci.getPriceAtAdd())
                    .quantity(ci.getQuantity())
                    .subtotal(ci.getSubtotal())
                    .build();
        }).toList();

        // Tính lại total từ items hiện tại
        BigDecimal calculatedTotal = items.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponse.builder()
                .cartId(cart.getId())
                .items(items)
                .total(calculatedTotal)
                .build();
    }

    @Override
    public CartResponse getMyCart() {
        User user = currentUser();
        Cart cart = getOrCreateCart(user);
        return toResponse(cart);
    }

    @Override
    public CartResponse addItem(AddToCartRequest req) {
        User user = currentUser();
        Cart cart = getOrCreateCart(user);

        Product product = productRepository.findById(req.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        // Nếu đã có item cùng product → tăng số lượng
        CartItem item = cart.getItems()
                .stream()
                .filter(ci -> ci.getProduct().getId().equals(product.getId()))
                .findFirst()
                .orElseGet(() -> {
                    CartItem ci = CartItem.builder()
                            .cart(cart)
                            .product(product)
                            .quantity(0)
                            .priceAtAdd(product.getPrice())
                            .build();
                    cart.getItems().add(ci);
                    return ci;
                });

        item.setQuantity(item.getQuantity() + req.getQuantity());
        cart.recalcTotal();

        Cart savedCart = cartRepository.save(cart);
        return toResponse(savedCart);
    }

    @Override
    public CartResponse updateItem(Integer itemId, UpdateCartItemRequest req) {
        User user = currentUser();
        Cart cart = getOrCreateCart(user);

        CartItem item = cartItemRepository.findByIdAndCart_Id(itemId, cart.getId())
                .orElseThrow(() -> new EntityNotFoundException("Cart item not found"));

        if (req.getQuantity() <= 0) {
            // Xóa item nếu quantity <= 0
            cart.getItems().remove(item);
            cartItemRepository.delete(item);
        } else {
            // Cập nhật quantity
            item.setQuantity(req.getQuantity());
        }

        cart.recalcTotal();
        Cart savedCart = cartRepository.save(cart);
        return toResponse(savedCart);
    }

    @Override
    public CartResponse removeItem(Integer itemId) {
        User user = currentUser();
        Cart cart = getOrCreateCart(user);

        CartItem item = cartItemRepository.findByIdAndCart_Id(itemId, cart.getId())
                .orElseThrow(() -> new EntityNotFoundException("Cart item not found"));

        cart.getItems().remove(item);
        cartItemRepository.delete(item);
        cart.recalcTotal();

        Cart savedCart = cartRepository.save(cart);
        return toResponse(savedCart);
    }

    @Override
    public CartResponse clear() {
        User user = currentUser();
        Cart cart = getOrCreateCart(user);

        cart.getItems().clear(); // orphanRemoval = true → xóa item
        cart.recalcTotal();
        return toResponse(cartRepository.save(cart));
    }
}
