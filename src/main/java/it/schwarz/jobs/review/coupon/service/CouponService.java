package it.schwarz.jobs.review.coupon.service;

import it.schwarz.jobs.review.coupon.domain.model.ApplicationResult;
import it.schwarz.jobs.review.coupon.domain.model.Basket;
import it.schwarz.jobs.review.coupon.domain.model.Coupon;
import it.schwarz.jobs.review.coupon.domain.model.CouponApplications;
import it.schwarz.jobs.review.coupon.domain.exception.BasketValueTooLowException;
import it.schwarz.jobs.review.coupon.domain.exception.CouponAlreadyExistsException;
import it.schwarz.jobs.review.coupon.domain.exception.CouponCodeNotFoundException;
import it.schwarz.jobs.review.coupon.provider.CouponProvider;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CouponService
{

    private final CouponProvider couponProvider;

    public CouponService(CouponProvider couponProvider) {
        this.couponProvider = couponProvider;
    }

    @Transactional
    public Coupon createCoupon(Coupon coupon) {
        try {
            return couponProvider.createCoupon(coupon);
        } catch (IllegalStateException ex) {
            throw new CouponAlreadyExistsException(ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<Coupon> findAllCoupons() {
        return couponProvider.findAll();
    }

    @Transactional(readOnly = true)
    public CouponApplications getApplications(String couponCode) {
        var foundCouponApplications = couponProvider.getCouponApplications(couponCode);
        if (foundCouponApplications.isEmpty()) {
            throw new CouponCodeNotFoundException("Coupon-Code " + couponCode + " not found.");
        }
        return foundCouponApplications.get();
    }

    @Transactional  // need to validate
    public ApplicationResult applyCoupon(Basket basket, String couponCode) {

        var basketValue = basket.getValue();
        var foundCoupon = couponProvider.findById(couponCode);

        // No Coupon found for given Coupon Code
        if (foundCoupon.isEmpty()) {
            throw new CouponCodeNotFoundException("Coupon-Code " + couponCode + " not found.");
        }

        // Basket value must not be less than discount
        var couponToApply = foundCoupon.get();
        if (basketValue.isLessThan(couponToApply.getDiscount())) {
            throw new BasketValueTooLowException(
                    "The basket value (" + basketValue.toBigDecimal() + ") must not be less than the discount (" + couponToApply.getDiscount().toBigDecimal() + ").");
        }

        // Basket value must not be less than Coupon's minimal Basket Value
        if (basketValue.isLessThan(couponToApply.getMinBasketValue())) {
            throw new BasketValueTooLowException(
                    "The basket value (" + basketValue.toBigDecimal() + ") must not be less than the min. allowed basket value (" + couponToApply.getMinBasketValue().toBigDecimal() + ").");
        }

        // Register the usage of this coupon
        couponProvider.registerCouponApplication(couponToApply.getCode());

        // Apply
        return new ApplicationResult(basket, couponToApply);
    }

}
