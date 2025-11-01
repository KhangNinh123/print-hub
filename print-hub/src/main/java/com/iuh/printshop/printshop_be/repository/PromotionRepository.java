package com.iuh.printshop.printshop_be.repository;

import com.iuh.printshop.printshop_be.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Integer> {
    @Query("SELECT p FROM Promotion p WHERE p.isActive = true AND p.startDate <= :now AND p.endDate >= :now")
    List<Promotion> findActivePromotions(@Param("now") LocalDateTime now);

    @Query("SELECT p FROM Promotion p WHERE p.isActive = true AND p.startDate <= :now AND p.endDate >= :now AND :categoryId MEMBER OF p.applicableCategories")
    List<Promotion> findActivePromotionsByCategory(@Param("categoryId") Integer categoryId, @Param("now") LocalDateTime now);

    @Query("SELECT p FROM Promotion p WHERE p.isActive = true AND p.startDate <= :now AND p.endDate >= :now AND :productId MEMBER OF p.applicableProducts")
    List<Promotion> findActivePromotionsByProduct(@Param("productId") Integer productId, @Param("now") LocalDateTime now);
}
