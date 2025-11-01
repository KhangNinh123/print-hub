package com.iuh.printshop.printshop_be.dto.discount;

import com.iuh.printshop.printshop_be.entity.DiscountCode;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiscountCodeRequest {
    @NotBlank
    @Size(min = 3, max = 50)
    private String code;

    private String description;

    @NotNull
    private DiscountCode.DiscountType discountType;

    @NotNull
    @DecimalMin(value = "0.01", inclusive = false)
    private BigDecimal discountValue;

    @DecimalMin(value = "0.00", inclusive = true)
    private BigDecimal minOrderValue;

    @DecimalMin(value = "0.01", inclusive = false)
    private BigDecimal maxDiscount;

    @Min(1)
    private Integer usageLimit;

    @NotNull
    private LocalDateTime startDate;

    @NotNull
    private LocalDateTime endDate;

    @Builder.Default
    private Boolean isActive = true;
}
