package com.iuh.printshop.printshop_be.Service;

import com.iuh.printshop.printshop_be.Repository.FeedbackRepository;
import com.iuh.printshop.printshop_be.model.Feedback;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service @RequiredArgsConstructor
public class FeedbackService {
    private final FeedbackRepository repo;

    public Page<Feedback> listAll(int page, int size) {
        return repo.findAll(PageRequest.of(page, size, Sort.by("id").descending()));
    }
    public Page<Feedback> listMine(Integer userId, int page, int size) {
        return repo.findByUserId(userId, PageRequest.of(page, size, Sort.by("id").descending()));
    }
    public Page<Feedback> listByOrder(Long orderId, int page, int size) {
        return repo.findByOrderId(orderId, PageRequest.of(page, size, Sort.by("id").descending()));
    }

    public Feedback create(Integer userId, String type, String subject, String content, Integer rating, Long orderId) {
        var t = Feedback.Type.valueOf(type.toUpperCase());
        if (rating != null && (rating < 1 || rating > 5)) throw new IllegalArgumentException("rating phải 1..5");
        return repo.save(Feedback.builder()
                .userId(userId).orderId(orderId).type(t).rating(rating)
                .subject(subject).content(content).build());
    }
}
