package com.iuh.printshop.printshop_be.dto.promotion;

import com.iuh.printshop.printshop_be.entity.Promotion;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromotionRequest {
    @NotBlank
    @Size(min = 3, max = 100)
    private String name;

    private String description;

    @NotNull
    private Promotion.DiscountType discountType;

    @NotNull
    @DecimalMin(value = "0.01", inclusive = false)
    private BigDecimal discountValue;

    private List<Integer> applicableCategoryIds;

    private List<Integer> applicableProductIds;

    @DecimalMin(value = "0.00", inclusive = true)
    private BigDecimal minOrderValue;

    @DecimalMin(value = "0.01", inclusive = false)
    private BigDecimal maxDiscount;

    @NotNull
    private LocalDateTime startDate;

    @NotNull
    private LocalDateTime endDate;

    @Builder.Default
    private Boolean isActive = true;
}
