package it.schwarz.jobs.review.coupon.provider;

import it.schwarz.jobs.review.coupon.domain.model.Coupon;
import it.schwarz.jobs.review.coupon.domain.model.CouponApplications;

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
