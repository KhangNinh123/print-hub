package com.iuh.printshop.printshop_be.Repository;

import com.iuh.printshop.printshop_be.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    Optional<CartItem> findByIdAndCart_Id(Integer id, Integer cartId);
}
