package com.iuh.printshop.printshop_be.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity @Table(name = "feedbacks")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Feedback {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_id")  private Integer userId;     // nullable
    @Column(name="order_id") private Long orderId;       // nullable

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('PRODUCT','SHOP','DELIVERY','OTHER')")
    private Type type;

    public enum Type { PRODUCT, SHOP, DELIVERY, OTHER }

    private Integer rating;                              // nullable 1..5
    @Column(length = 150) private String subject;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "created_at", updatable = false, insertable = false)
    private Instant createdAt;
}
