package it.schwarz.jobs.review.coupon.domain.exception;

public class BusinessException extends RuntimeException {
    public BusinessException(String detail) {
        super(detail);
    }

}
