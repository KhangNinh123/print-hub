package com.iuh.printshop.printshop_be.service.impl;

import com.iuh.printshop.printshop_be.dto.discount.DiscountCalculationResult;
import com.iuh.printshop.printshop_be.dto.promotion.PromotionRequest;
import com.iuh.printshop.printshop_be.dto.promotion.PromotionResponse;
import com.iuh.printshop.printshop_be.entity.Category;
import com.iuh.printshop.printshop_be.entity.Product;
import com.iuh.printshop.printshop_be.entity.Promotion;
import com.iuh.printshop.printshop_be.repository.CategoryRepository;
import com.iuh.printshop.printshop_be.repository.ProductRepository;
import com.iuh.printshop.printshop_be.repository.PromotionRepository;
import com.iuh.printshop.printshop_be.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    public PromotionResponse createPromotion(PromotionRequest request) {
        Promotion promotion = Promotion.builder()
                .name(request.getName())
                .description(request.getDescription())
                .discountType(request.getDiscountType())
                .discountValue(request.getDiscountValue())
                .minOrderValue(request.getMinOrderValue())
                .maxDiscount(request.getMaxDiscount())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .isActive(request.getIsActive())
                .build();

        if (request.getApplicableCategoryIds() != null) {
            List<Category> categories = categoryRepository.findAllById(request.getApplicableCategoryIds());
            promotion.setApplicableCategories(categories);
        }

        if (request.getApplicableProductIds() != null) {
            List<Product> products = productRepository.findAllById(request.getApplicableProductIds());
            promotion.setApplicableProducts(products);
        }

        Promotion saved = promotionRepository.save(promotion);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromotionResponse> getAllPromotions() {
        return promotionRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PromotionResponse> getPromotionById(Integer id) {
        return promotionRepository.findById(id).map(this::toResponse);
    }

    @Override
    public Optional<PromotionResponse> updatePromotion(Integer id, PromotionRequest request) {
        return promotionRepository.findById(id).map(promotion -> {
            promotion.setName(request.getName());
            promotion.setDescription(request.getDescription());
            promotion.setDiscountType(request.getDiscountType());
            promotion.setDiscountValue(request.getDiscountValue());
            promotion.setMinOrderValue(request.getMinOrderValue());
            promotion.setMaxDiscount(request.getMaxDiscount());
            promotion.setStartDate(request.getStartDate());
            promotion.setEndDate(request.getEndDate());
            promotion.setIsActive(request.getIsActive());

            if (request.getApplicableCategoryIds() != null) {
                List<Category> categories = categoryRepository.findAllById(request.getApplicableCategoryIds());
                promotion.setApplicableCategories(categories);
            } else {
                promotion.getApplicableCategories().clear();
            }

            if (request.getApplicableProductIds() != null) {
                List<Product> products = productRepository.findAllById(request.getApplicableProductIds());
                promotion.setApplicableProducts(products);
            } else {
                promotion.getApplicableProducts().clear();
            }

            Promotion saved = promotionRepository.save(promotion);
            return toResponse(saved);
        });
    }

    @Override
    public boolean deletePromotion(Integer id) {
        if (promotionRepository.existsById(id)) {
            promotionRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromotionResponse> getActivePromotions() {
        return promotionRepository.findActivePromotions(LocalDateTime.now()).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DiscountCalculationResult calculatePromotionDiscount(BigDecimal orderTotal, List<Integer> productIds, List<Integer> categoryIds) {
        List<Promotion> activePromotions = promotionRepository.findActivePromotions(LocalDateTime.now());

        BigDecimal maxDiscount = BigDecimal.ZERO;
        Promotion bestPromotion = null;

        for (Promotion promotion : activePromotions) {
            boolean applies = false;

            if (productIds != null && !productIds.isEmpty()) {
                for (Integer productId : productIds) {
                    if (promotion.getApplicableProducts().stream().anyMatch(p -> p.getId().equals(productId))) {
                        applies = true;
                        break;
                    }
                }
            }

            if (!applies && categoryIds != null && !categoryIds.isEmpty()) {
                for (Integer categoryId : categoryIds) {
                    if (promotion.getApplicableCategories().stream().anyMatch(c -> c.getId().equals(categoryId))) {
                        applies = true;
                        break;
                    }
                }
            }

            if (!applies && (promotion.getApplicableProducts().isEmpty() && promotion.getApplicableCategories().isEmpty())) {
                applies = true;
            }

            if (applies) {
                if (promotion.getMinOrderValue() == null || orderTotal.compareTo(promotion.getMinOrderValue()) >= 0) {
                    BigDecimal discountAmount = calculateDiscountAmount(promotion, orderTotal);

                    if (promotion.getMaxDiscount() != null && discountAmount.compareTo(promotion.getMaxDiscount()) > 0) {
                        discountAmount = promotion.getMaxDiscount();
                    }

                    if (discountAmount.compareTo(maxDiscount) > 0) {
                        maxDiscount = discountAmount;
                        bestPromotion = promotion;
                    }
                }
            }
        }

        BigDecimal finalTotal = orderTotal.subtract(maxDiscount);

        return DiscountCalculationResult.builder()
                .originalTotal(orderTotal)
                .discountAmount(maxDiscount)
                .finalTotal(finalTotal)
                .discountCode(bestPromotion != null ? bestPromotion.getName() : null)
                .discountDescription(bestPromotion != null ? bestPromotion.getDescription() : null)
                .build();
    }

    private BigDecimal calculateDiscountAmount(Promotion promotion, BigDecimal orderTotal) {
        if (promotion.getDiscountType() == Promotion.DiscountType.PERCENTAGE) {
            return orderTotal.multiply(promotion.getDiscountValue().divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
        } else {
            return promotion.getDiscountValue();
        }
    }

    private PromotionResponse toResponse(Promotion promotion) {
        return PromotionResponse.builder()
                .id(promotion.getId())
                .name(promotion.getName())
                .description(promotion.getDescription())
                .discountType(promotion.getDiscountType())
                .discountValue(promotion.getDiscountValue())
                .applicableCategoryNames(promotion.getApplicableCategories().stream()
                        .map(Category::getName)
                        .collect(Collectors.toList()))
                .applicableProductNames(promotion.getApplicableProducts().stream()
                        .map(Product::getName)
                        .collect(Collectors.toList()))
                .minOrderValue(promotion.getMinOrderValue())
                .maxDiscount(promotion.getMaxDiscount())
                .startDate(promotion.getStartDate())
                .endDate(promotion.getEndDate())
                .isActive(promotion.getIsActive())
                .createdAt(promotion.getCreatedAt())
                .updatedAt(promotion.getUpdatedAt())
                .build();
    }
}
