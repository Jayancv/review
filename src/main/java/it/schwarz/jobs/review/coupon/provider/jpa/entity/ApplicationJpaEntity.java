package it.schwarz.jobs.review.coupon.provider.jpa.entity;


import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "APPLICATION")
public class ApplicationJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COUPON_CODE", nullable = false)
    private CouponJpaEntity coupon;

    @Column(name = "TIMESTAMP", nullable = false)
    private Instant timestamp;


    public ApplicationJpaEntity() {
    }

    public ApplicationJpaEntity(CouponJpaEntity code, Instant timestamp) {
        this.coupon = code;
        this.timestamp = timestamp;
    }

    public long getId() {
        return id;
    }

    public CouponJpaEntity getCoupon() {
        return coupon;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
