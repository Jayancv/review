package it.schwarz.jobs.review.coupon.provider.entity;


import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "APPLICATION")
public class ApplicationJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    // Add one-to-many relation
    @Column(name = "COUPON_CODE", nullable = false)
    private String couponCode;

    @Column(name = "TIMESTAMP", nullable = false)
    private Instant timestamp;


    public ApplicationJpaEntity() {
    }

    public ApplicationJpaEntity(String code, Instant timestamp) {
        this.couponCode = code;
        this.timestamp = timestamp;
    }

    public long getId() {
        return id;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
