package it.schwarz.jobs.review.coupon.domain.usecase.exception;

public class BusinessException extends RuntimeException {
    public BusinessException(String detail) {
        super(detail);
    }

}
