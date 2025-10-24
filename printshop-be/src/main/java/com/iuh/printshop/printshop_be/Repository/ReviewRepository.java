package com.iuh.printshop.printshop_be.Repository;

import com.iuh.printshop.printshop_be.model.Review;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByProductId(Integer productId, Pageable pageable);
    boolean existsByProductIdAndUserId(Integer productId, Integer userId);

    @Query("select coalesce(avg(r.rating),0) from Review r where r.productId=:pid")
    double avgRating(@Param("pid") Integer productId);

    @Query("select count(r) from Review r where r.productId=:pid")
    long countByProduct(@Param("pid") Integer productId);
}
