package com.iuh.printshop.printshop_be.dto.promotion;

import com.iuh.printshop.printshop_be.entity.Promotion;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromotionResponse {
    private Integer id;
    private String name;
    private String description;
    private Promotion.DiscountType discountType;
    private BigDecimal discountValue;
    private List<String> applicableCategoryNames;
    private List<String> applicableProductNames;
    private BigDecimal minOrderValue;
    private BigDecimal maxDiscount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
