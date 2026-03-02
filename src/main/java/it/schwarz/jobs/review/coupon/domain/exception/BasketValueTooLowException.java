package it.schwarz.jobs.review.coupon.domain.exception;

import org.springframework.http.HttpStatus;

public class BasketValueTooLowException extends BusinessException {
    public BasketValueTooLowException(String detail)
    {
        super(detail, HttpStatus.BAD_REQUEST);
    }
}
