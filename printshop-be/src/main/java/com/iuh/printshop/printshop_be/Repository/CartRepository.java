package com.iuh.printshop.printshop_be.Repository;

import com.iuh.printshop.printshop_be.entity.Cart;
import com.iuh.printshop.printshop_be.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Integer> {
    Optional<Cart> findByUser(User user);
    boolean existsByUser(User user);
}
