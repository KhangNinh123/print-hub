package com.iuh.printshop.printshop_be.dto.cart;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponse {
    private Integer cartId;
    private List<CartItemResponse> items;
    private BigDecimal total;
}
