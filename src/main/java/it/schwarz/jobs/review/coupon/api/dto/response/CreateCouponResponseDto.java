package it.schwarz.jobs.review.coupon.api.dto.response;

import it.schwarz.jobs.review.coupon.api.dto.common.CouponDto;
import it.schwarz.jobs.review.coupon.domain.model.Coupon;

public record CreateCouponResponseDto(CouponDto coupon) {

    public static CreateCouponResponseDto of(Coupon coupon) {
        return new CreateCouponResponseDto(
                new CouponDto(
                        coupon.getCode(),
                        coupon.getDiscount().toBigDecimal(),
                        coupon.getMinBasketValue().toBigDecimal(),
                        coupon.getDescription(),
                        coupon.getApplicationCount()));
    }
}
