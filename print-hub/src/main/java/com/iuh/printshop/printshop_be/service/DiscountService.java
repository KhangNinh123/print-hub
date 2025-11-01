package com.iuh.printshop.printshop_be.service;

import com.iuh.printshop.printshop_be.dto.discount.ApplyDiscountRequest;
import com.iuh.printshop.printshop_be.dto.discount.DiscountCalculationResult;
import com.iuh.printshop.printshop_be.dto.discount.DiscountCodeRequest;
import com.iuh.printshop.printshop_be.dto.discount.DiscountCodeResponse;
import com.iuh.printshop.printshop_be.entity.DiscountCode;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface DiscountService {
    DiscountCodeResponse createDiscountCode(DiscountCodeRequest request);
    List<DiscountCodeResponse> getAllDiscountCodes();
    Optional<DiscountCodeResponse> getDiscountCodeById(Integer id);
    Optional<DiscountCodeResponse> getDiscountCodeByCode(String code);
    Optional<DiscountCodeResponse> updateDiscountCode(Integer id, DiscountCodeRequest request);
    boolean deleteDiscountCode(Integer id);
    DiscountCalculationResult applyDiscountCode(String code, BigDecimal orderTotal);
    boolean validateDiscountCode(String code, BigDecimal orderTotal);
}
