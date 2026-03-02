package it.schwarz.jobs.review.coupon.testobjects;

import it.schwarz.jobs.review.coupon.api.dto.request.ApplyCouponRequestDto;
import it.schwarz.jobs.review.coupon.api.dto.common.BasketDto;
import it.schwarz.jobs.review.coupon.api.dto.request.CreateCouponRequestDto;

import java.math.BigDecimal;

public class TestRequests {

    public CreateCouponRequestDto validCoupon() {
        return new CreateCouponRequestDto("CODE_12_20", new BigDecimal("12.00"), new BigDecimal("20.00"), "12 for 20");
    }

    public CreateCouponRequestDto invalidCouponOfNegativeDiscount() {
        return new CreateCouponRequestDto("CODE_12_20", new BigDecimal("-12.00"), new BigDecimal("20.00"), "12 for 20");
    }

    public ApplyCouponRequestDto validApplication() {
        return new ApplyCouponRequestDto(new BasketDto(new BigDecimal("60.00")), "TEST_05_50");
    }

    public ApplyCouponRequestDto invalidApplicationOfNotExistingCode() {
        return new ApplyCouponRequestDto(new BasketDto(new BigDecimal("60.00")), "<NOT-EXISTING-CODE>");
    }

    public ApplyCouponRequestDto invalidApplicationOfLowBasketValue() {
        return new ApplyCouponRequestDto(new BasketDto(new BigDecimal("20.00")), "TEST_05_50");
    }
}
