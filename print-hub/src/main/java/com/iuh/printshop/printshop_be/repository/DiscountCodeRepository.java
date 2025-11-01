package com.iuh.printshop.printshop_be.repository;

import com.iuh.printshop.printshop_be.entity.DiscountCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface DiscountCodeRepository extends JpaRepository<DiscountCode, Integer> {
    Optional<DiscountCode> findByCode(String code);

    @Query("SELECT dc FROM DiscountCode dc WHERE dc.code = :code AND dc.isActive = true AND dc.startDate <= :now AND dc.endDate >= :now")
    Optional<DiscountCode> findActiveByCode(@Param("code") String code, @Param("now") LocalDateTime now);

    @Query("SELECT dc FROM DiscountCode dc WHERE dc.code = :code AND dc.isActive = true AND dc.startDate <= :now AND dc.endDate >= :now AND (dc.usageLimit IS NULL OR dc.usedCount < dc.usageLimit)")
    Optional<DiscountCode> findValidByCode(@Param("code") String code, @Param("now") LocalDateTime now);
}
