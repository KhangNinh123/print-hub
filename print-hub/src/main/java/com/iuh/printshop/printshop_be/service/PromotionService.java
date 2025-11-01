package com.iuh.printshop.printshop_be.service;

import com.iuh.printshop.printshop_be.dto.discount.DiscountCalculationResult;
import com.iuh.printshop.printshop_be.dto.promotion.PromotionRequest;
import com.iuh.printshop.printshop_be.dto.promotion.PromotionResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface PromotionService {
    PromotionResponse createPromotion(PromotionRequest request);
    List<PromotionResponse> getAllPromotions();
    Optional<PromotionResponse> getPromotionById(Integer id);
    Optional<PromotionResponse> updatePromotion(Integer id, PromotionRequest request);
    boolean deletePromotion(Integer id);
    List<PromotionResponse> getActivePromotions();
    DiscountCalculationResult calculatePromotionDiscount(BigDecimal orderTotal, List<Integer> productIds, List<Integer> categoryIds);
}
