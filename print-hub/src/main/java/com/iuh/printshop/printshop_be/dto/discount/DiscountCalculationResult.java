package com.iuh.printshop.printshop_be.dto.discount;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiscountCalculationResult {
    private BigDecimal originalTotal;
    private BigDecimal discountAmount;
    private BigDecimal finalTotal;
    private String discountCode;
    private String discountDescription;
}
