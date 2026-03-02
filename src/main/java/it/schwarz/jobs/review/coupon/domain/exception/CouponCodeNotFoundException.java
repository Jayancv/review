package it.schwarz.jobs.review.coupon.domain.exception;

import org.springframework.http.HttpStatus;

public class CouponCodeNotFoundException extends BusinessException {
    public CouponCodeNotFoundException(String detail)
    {
        super(detail, HttpStatus.NOT_FOUND);
    }
}
