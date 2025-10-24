package com.iuh.printshop.printshop_be.Repository;

import com.iuh.printshop.printshop_be.model.Feedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    Page<Feedback> findByUserId(Integer userId, Pageable pageable);
    Page<Feedback> findByOrderId(Long orderId, Pageable pageable);
}
