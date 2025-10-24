package com.iuh.printshop.printshop_be.Service;

import com.iuh.printshop.printshop_be.Repository.ReviewRepository;
import com.iuh.printshop.printshop_be.model.Review;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository repo;

    public Page<Review> listByProduct(Integer productId, int page, int size) {
        return repo.findByProductId(productId, PageRequest.of(page, size, Sort.by("id").descending()));
    }

    public record RatingSummary(double average, long count) {}
    public RatingSummary summary(Integer productId) {
        return new RatingSummary(repo.avgRating(productId), repo.countByProduct(productId));
    }

    @Transactional
    public Review create(Integer userId, Integer productId, int rating, String title, String content) {
        if (rating < 1 || rating > 5) throw new IllegalArgumentException("rating phải 1..5");
        if (repo.existsByProductIdAndUserId(productId, userId))
            throw new IllegalStateException("Bạn đã đánh giá sản phẩm này rồi");
        var r = Review.builder()
                .productId(productId).userId(userId).rating(rating)
                .title(title).content(content).build();
        return repo.save(r);
    }

    @Transactional
    public Review update(Long id, Integer userId, int rating, String title, String content, boolean isAdmin) {
        var r = repo.findById(id).orElseThrow();
        if (!isAdmin && !r.getUserId().equals(userId))
            throw new SecurityException("Không được sửa review của người khác");
        if (rating < 1 || rating > 5) throw new IllegalArgumentException("rating phải 1..5");
        r.setRating(rating); r.setTitle(title); r.setContent(content);
        return repo.save(r);
    }

    @Transactional
    public void delete(Long id, Integer userId, boolean isAdmin) {
        var r = repo.findById(id).orElseThrow();
        if (!isAdmin && !r.getUserId().equals(userId))
            throw new SecurityException("Không được xoá review của người khác");
        repo.deleteById(id);
    }
}
