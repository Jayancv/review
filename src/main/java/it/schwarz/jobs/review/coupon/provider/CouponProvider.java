package it.schwarz.jobs.review.coupon.provider;

import it.schwarz.jobs.review.coupon.domain.entity.Coupon;
import it.schwarz.jobs.review.coupon.domain.entity.CouponApplications;

import java.util.List;
import java.util.Optional;

public interface CouponProvider {
    Coupon createCoupon(Coupon coupon);

    List<Coupon> findAll();

    Optional<Coupon> findById(String couponCode);

    void registerCouponApplication(String couponCode);

    Optional<CouponApplications> getCouponApplications(String couponCode);

    void reset();
}
