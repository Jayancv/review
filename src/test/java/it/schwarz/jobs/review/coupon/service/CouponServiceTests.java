package it.schwarz.jobs.review.coupon.service;

import it.schwarz.jobs.review.coupon.domain.usecase.exception.CouponAlreadyExistsException;
import it.schwarz.jobs.review.coupon.provider.inmem.InMemoryCouponProvider;
import it.schwarz.jobs.review.coupon.testobjects.TestObjects;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CouponServiceTests
{

    @Test
    void testFindAllCoupons() {
        var couponUseCases = new CouponService(new InMemoryCouponProvider());
        var allCoupons = couponUseCases.findAllCoupons();
        assertThat(allCoupons).hasSize(3);
    }

    @Test
    void testCreateCoupon() {
        // Create
        var couponUseCases = new CouponService(new InMemoryCouponProvider());
        var createdCoupon = couponUseCases.createCoupon(TestObjects.coupons().COUPON_12_20());
        assertThat(createdCoupon).isNotNull();

        // Duplicate
        Exception exception = assertThrows(
            CouponAlreadyExistsException.class, () -> couponUseCases.createCoupon(TestObjects.coupons().COUPON_12_20()));
        assertThat(exception.getMessage()).contains("Coupon already exists");
    }

}