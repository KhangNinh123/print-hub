package com.iuh.printshop.printshop_be.dto.discount;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplyDiscountRequest {
    @NotBlank
    private String code;
}
