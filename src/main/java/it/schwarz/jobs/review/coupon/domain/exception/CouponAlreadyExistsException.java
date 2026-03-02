package it.schwarz.jobs.review.coupon.domain.exception;

import org.springframework.http.HttpStatus;

public class CouponAlreadyExistsException extends BusinessException {
    public CouponAlreadyExistsException(String detail)
    {
        super(detail, HttpStatus.CONFLICT);
    }
}
