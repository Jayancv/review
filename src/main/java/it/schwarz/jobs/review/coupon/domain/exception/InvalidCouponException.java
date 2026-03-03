package it.schwarz.jobs.review.coupon.domain.exception;

import org.springframework.http.HttpStatus;

public class InvalidCouponException extends BusinessException {
    public InvalidCouponException(String detail) {
        super(detail, HttpStatus.BAD_REQUEST);
    }
}