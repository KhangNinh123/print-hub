package com.iuh.printshop.printshop_be.service;

import com.iuh.printshop.printshop_be.dto.cart.AddToCartRequest;
import com.iuh.printshop.printshop_be.dto.cart.CartResponse;
import com.iuh.printshop.printshop_be.dto.cart.UpdateCartItemRequest;

public interface CartService {
    CartResponse getMyCart();
    CartResponse addItem(AddToCartRequest req);
    CartResponse updateItem(Integer itemId, UpdateCartItemRequest req);
    CartResponse removeItem(Integer itemId);
    CartResponse clear();
}
