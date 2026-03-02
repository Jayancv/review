package it.schwarz.jobs.review.coupon.domain.usecase.exception;

public class CouponCodeNotFoundException extends BusinessException {
    public CouponCodeNotFoundException(String detail) {
        super(detail);
    }
}
