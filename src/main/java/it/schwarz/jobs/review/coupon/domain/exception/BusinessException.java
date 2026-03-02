package it.schwarz.jobs.review.coupon.domain.exception;

import org.springframework.http.HttpStatus;

public class BusinessException extends RuntimeException {
    private final HttpStatus status;

    public BusinessException(String detail, HttpStatus status) {
        super(detail);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

}
