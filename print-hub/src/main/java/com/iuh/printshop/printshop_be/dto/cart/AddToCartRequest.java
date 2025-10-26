package com.iuh.printshop.printshop_be.dto.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddToCartRequest {
    @NotNull
    private Integer productId;

    @NotNull
    @Min(1)
    private Integer quantity = 1;
}
