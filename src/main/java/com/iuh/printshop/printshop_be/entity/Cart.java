package com.iuh.printshop.printshop_be.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carts")
@Getter @Setter
@Builder
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CartItem> items = new ArrayList<>();

    @Column(nullable = false, precision = 16, scale = 2)
    @Builder.Default
    private BigDecimal total = BigDecimal.ZERO;

    // Constructor để đảm bảo items luôn được khởi tạo
    public Cart() {
        this.items = new ArrayList<>();
        this.total = BigDecimal.ZERO;
    }

    // Constructor với Builder để đảm bảo items không null
    @Builder
    public Cart(Integer id, User user, List<CartItem> items, BigDecimal total) {
        this.id = id;
        this.user = user;
        this.items = items != null ? items : new ArrayList<>();
        this.total = total != null ? total : BigDecimal.ZERO;
    }

    public void recalcTotal() {
        this.total = items.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
