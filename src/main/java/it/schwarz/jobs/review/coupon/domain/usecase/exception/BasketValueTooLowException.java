package it.schwarz.jobs.review.coupon.domain.usecase.exception;

public class BasketValueTooLowException extends BusinessException {
    public BasketValueTooLowException(String detail) {
        super(detail);
    }
}
