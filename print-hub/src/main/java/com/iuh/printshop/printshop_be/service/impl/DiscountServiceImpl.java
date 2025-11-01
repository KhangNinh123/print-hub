package com.iuh.printshop.printshop_be.service.impl;

import com.iuh.printshop.printshop_be.dto.discount.DiscountCalculationResult;
import com.iuh.printshop.printshop_be.dto.discount.DiscountCodeRequest;
import com.iuh.printshop.printshop_be.dto.discount.DiscountCodeResponse;
import com.iuh.printshop.printshop_be.entity.DiscountCode;
import com.iuh.printshop.printshop_be.repository.DiscountCodeRepository;
import com.iuh.printshop.printshop_be.service.DiscountService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
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
public class DiscountServiceImpl implements DiscountService {

    private final DiscountCodeRepository discountCodeRepository;

    @Override
    public DiscountCodeResponse createDiscountCode(DiscountCodeRequest request) {
        if (discountCodeRepository.findByCode(request.getCode()).isPresent()) {
            throw new IllegalArgumentException("Discount code already exists: " + request.getCode());
        }

        DiscountCode discountCode = DiscountCode.builder()
                .code(request.getCode())
                .description(request.getDescription())
                .discountType(request.getDiscountType())
                .discountValue(request.getDiscountValue())
                .minOrderValue(request.getMinOrderValue())
                .maxDiscount(request.getMaxDiscount())
                .usageLimit(request.getUsageLimit())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .isActive(request.getIsActive())
                .build();

        DiscountCode saved = discountCodeRepository.save(discountCode);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DiscountCodeResponse> getAllDiscountCodes() {
        return discountCodeRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DiscountCodeResponse> getDiscountCodeById(Integer id) {
        return discountCodeRepository.findById(id).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DiscountCodeResponse> getDiscountCodeByCode(String code) {
        return discountCodeRepository.findByCode(code).map(this::toResponse);
    }

    @Override
    public Optional<DiscountCodeResponse> updateDiscountCode(Integer id, DiscountCodeRequest request) {
        return discountCodeRepository.findById(id).map(discountCode -> {
            if (!discountCode.getCode().equals(request.getCode()) &&
                discountCodeRepository.findByCode(request.getCode()).isPresent()) {
                throw new IllegalArgumentException("Discount code already exists: " + request.getCode());
            }

            discountCode.setCode(request.getCode());
            discountCode.setDescription(request.getDescription());
            discountCode.setDiscountType(request.getDiscountType());
            discountCode.setDiscountValue(request.getDiscountValue());
            discountCode.setMinOrderValue(request.getMinOrderValue());
            discountCode.setMaxDiscount(request.getMaxDiscount());
            discountCode.setUsageLimit(request.getUsageLimit());
            discountCode.setStartDate(request.getStartDate());
            discountCode.setEndDate(request.getEndDate());
            discountCode.setIsActive(request.getIsActive());

            DiscountCode saved = discountCodeRepository.save(discountCode);
            return toResponse(saved);
        });
    }

    @Override
    public boolean deleteDiscountCode(Integer id) {
        if (discountCodeRepository.existsById(id)) {
            discountCodeRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public DiscountCalculationResult applyDiscountCode(String code, BigDecimal orderTotal) {
        Optional<DiscountCode> discountCodeOpt = discountCodeRepository.findValidByCode(code, LocalDateTime.now());

        if (discountCodeOpt.isEmpty()) {
            throw new IllegalArgumentException("Invalid or expired discount code: " + code);
        }

        DiscountCode discountCode = discountCodeOpt.get();

        if (discountCode.getMinOrderValue() != null && orderTotal.compareTo(discountCode.getMinOrderValue()) < 0) {
            throw new IllegalArgumentException("Order total does not meet minimum requirement for discount code: " + code);
        }

        BigDecimal discountAmount = calculateDiscountAmount(discountCode, orderTotal);

        if (discountCode.getMaxDiscount() != null && discountAmount.compareTo(discountCode.getMaxDiscount()) > 0) {
            discountAmount = discountCode.getMaxDiscount();
        }

        BigDecimal finalTotal = orderTotal.subtract(discountAmount);

        return DiscountCalculationResult.builder()
                .originalTotal(orderTotal)
                .discountAmount(discountAmount)
                .finalTotal(finalTotal)
                .discountCode(code)
                .discountDescription(discountCode.getDescription())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validateDiscountCode(String code, BigDecimal orderTotal) {
        try {
            applyDiscountCode(code, orderTotal);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private BigDecimal calculateDiscountAmount(DiscountCode discountCode, BigDecimal orderTotal) {
        if (discountCode.getDiscountType() == DiscountCode.DiscountType.PERCENTAGE) {
            return orderTotal.multiply(discountCode.getDiscountValue().divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
        } else {
            return discountCode.getDiscountValue();
        }
    }

    private DiscountCodeResponse toResponse(DiscountCode discountCode) {
        return DiscountCodeResponse.builder()
                .id(discountCode.getId())
                .code(discountCode.getCode())
                .description(discountCode.getDescription())
                .discountType(discountCode.getDiscountType())
                .discountValue(discountCode.getDiscountValue())
                .minOrderValue(discountCode.getMinOrderValue())
                .maxDiscount(discountCode.getMaxDiscount())
                .usageLimit(discountCode.getUsageLimit())
                .usedCount(discountCode.getUsedCount())
                .startDate(discountCode.getStartDate())
                .endDate(discountCode.getEndDate())
                .isActive(discountCode.getIsActive())
                .createdAt(discountCode.getCreatedAt())
                .updatedAt(discountCode.getUpdatedAt())
                .build();
    }
}
