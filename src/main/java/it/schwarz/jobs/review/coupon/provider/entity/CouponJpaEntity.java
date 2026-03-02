package it.schwarz.jobs.review.coupon.provider.entity;


import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "COUPON")
public class CouponJpaEntity {

    @Id
    @Column(name = "CODE", nullable = false)
    private String code;
    @Column(name = "DISCOUNT", nullable = false, precision = 10, scale = 2)
    private BigDecimal discount;
    @Column(name = "MIN_BASKET_VALUE", nullable = false, precision = 10, scale = 2)
    private BigDecimal minBasketValue;
    @Column(name = "DESCRIPTION", nullable = false, length = 1000)
    private String description;
    @OneToMany(mappedBy = "couponCode")
    private List<ApplicationJpaEntity> applications;

    public CouponJpaEntity() {
    }

    public CouponJpaEntity(String code, BigDecimal discount, BigDecimal minBasketValue, String description) {
        this.code = code;
        this.discount = discount;
        this.minBasketValue = minBasketValue;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public BigDecimal getMinBasketValue() {
        return minBasketValue;
    }

    public String getDescription() {
        return description;
    }

    public List<ApplicationJpaEntity> getApplications() {
        return applications;
    }
}
