package com.iuh.printshop.printshop_be.Service;

import com.iuh.printshop.printshop_be.dto.cart.*;

public interface CartService {
    CartResponse getMyCart();
    CartResponse addItem(AddToCartRequest req);
    CartResponse updateItem(Integer itemId, UpdateCartItemRequest req);
    CartResponse removeItem(Integer itemId);
    CartResponse clear();
}
