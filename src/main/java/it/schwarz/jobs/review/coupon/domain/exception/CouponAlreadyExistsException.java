package it.schwarz.jobs.review.coupon.domain.exception;

public class CouponAlreadyExistsException extends BusinessException {
    public CouponAlreadyExistsException(String detail) {
        super(detail);
    }
}
