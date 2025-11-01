package com.iuh.printshop.printshop_be.service.impl;

import com.iuh.printshop.printshop_be.entity.Cart;
import com.iuh.printshop.printshop_be.entity.CartItem;
import com.iuh.printshop.printshop_be.repository.CartRepository;
import com.iuh.printshop.printshop_be.dto.payment.CheckoutInitResponse;
import com.iuh.printshop.printshop_be.dto.payment.CheckoutRequest;
import com.iuh.printshop.printshop_be.dto.payment.InitPaymentResult;
import com.iuh.printshop.printshop_be.entity.*;
import com.iuh.printshop.printshop_be.repository.OrderRepository;
import com.iuh.printshop.printshop_be.repository.PaymentRepository;
import com.iuh.printshop.printshop_be.service.CheckoutService;
import com.iuh.printshop.printshop_be.service.DiscountService;
import com.iuh.printshop.printshop_be.service.PaymentGateway;
import com.iuh.printshop.printshop_be.service.PromotionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CheckoutServiceImpl implements CheckoutService {

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final Map<PaymentMethod, PaymentGateway> paymentGateways;
    private final DiscountService discountService;
    private final PromotionService promotionService;

    @Transactional
    @Override
    public CheckoutInitResponse checkout(Integer userId, CheckoutRequest request) {
        Cart cart = cartRepository.findByUser(User.builder().id(userId).build())
                .orElseThrow(() -> new EntityNotFoundException("Cart not found for user=" + userId));
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        // Calculate original total
        BigDecimal originalTotal = cart.getTotal();

        // Apply discount code if provided
        BigDecimal discountAmount = BigDecimal.ZERO;
        String appliedDiscountCode = null;
        String discountDescription = null;

        if (request.getDiscountCode() != null && !request.getDiscountCode().trim().isEmpty()) {
            try {
                var discountResult = discountService.applyDiscountCode(request.getDiscountCode(), originalTotal);
                discountAmount = discountResult.getDiscountAmount();
                appliedDiscountCode = discountResult.getDiscountCode();
                discountDescription = discountResult.getDiscountDescription();
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid discount code: " + request.getDiscountCode());
            }
        }

        // Apply promotions
        List<Integer> productIds = cart.getItems().stream()
                .map(ci -> ci.getProduct().getId())
                .toList();
        List<Integer> categoryIds = cart.getItems().stream()
                .map(ci -> ci.getProduct().getCategory().getId())
                .distinct()
                .toList();

        var promotionResult = promotionService.calculatePromotionDiscount(originalTotal.subtract(discountAmount), productIds, categoryIds);
        BigDecimal promotionDiscount = promotionResult.getDiscountAmount();

        // Calculate final total
        BigDecimal finalTotal = originalTotal.subtract(discountAmount).subtract(promotionDiscount);

        // Snapshot Cart -> Order
        Order order = new Order();
        order.setUser(cart.getUser());
        order.setStatus(OrderStatus.PENDING);
        order.setTotal(finalTotal);
        order.setCurrency("VND");
        order.setNote("Discount: " + (appliedDiscountCode != null ? appliedDiscountCode : "None") +
                     ", Promotion: " + (promotionResult.getDiscountCode() != null ? promotionResult.getDiscountCode() : "None"));

        List<OrderItem> snapshot = new ArrayList<>();
        for (CartItem ci : cart.getItems()) {
            OrderItem oi = OrderItem.builder()
                    .order(order)
                    .product(ci.getProduct())
                    .quantity(ci.getQuantity())
                    .unitPrice(ci.getPriceAtAdd())
                    .id(new OrderItemId(order.getId().longValue(), ci.getProduct().getId()))
                    .build();
            snapshot.add(oi);
        }
        order.setItems(snapshot);
        order = orderRepository.save(order);

        // Create Payment
        Payment payment = Payment.builder()
                .order(order)
                .method(request.getMethod())
                .status(request.getMethod() == PaymentMethod.COD ? PaymentStatus.COD_PENDING : PaymentStatus.UNPAID)
                .amount(finalTotal)
                .currency(order.getCurrency())
                .build();

        // Init online gateway
        if (request.getMethod() != PaymentMethod.COD) {
            PaymentGateway gw = paymentGateways.get(request.getMethod());
            if (gw == null) throw new IllegalArgumentException("No gateway for method=" + request.getMethod());
            InitPaymentResult r = gw.initPayment(order, request.getSuccessUrl(), request.getCancelUrl());
            payment.setProviderTransactionId(r.getProviderTxId());
            payment.setCheckoutUrlOrClientSecret(r.getCheckoutUrlOrClientSecret());
            if (r.isRequiresAction()) payment.setStatus(PaymentStatus.REQUIRES_ACTION);
        }

        payment = paymentRepository.save(payment);

        return CheckoutInitResponse.builder()
                .orderId(order.getId())
                .paymentId(payment.getId())
                .method(payment.getMethod())
                .status(payment.getStatus())
                .paymentUrlOrClientSecret(payment.getCheckoutUrlOrClientSecret())
                .amount(finalTotal)
                .build();
    }
}
