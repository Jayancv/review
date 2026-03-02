package it.schwarz.jobs.review.coupon.api.handler;

import java.time.Instant;

import it.schwarz.jobs.review.coupon.domain.exception.BusinessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@ControllerAdvice
public class CouponExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {BusinessException.class})
    protected ResponseEntity<Object> handleBusinessExceptions(BusinessException ex, WebRequest request) {
        HttpStatus status = ex.getStatus();
        ProblemDetail problemDetail = ProblemDetail.forStatus(status);
        problemDetail.setDetail(ex.getMessage());
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("errorType", ex.getClass().getSimpleName());
        return handleExceptionInternal(ex, problemDetail, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(value = {Exception.class})
    protected ResponseEntity<Object> handleAllOtherExceptions(Exception ex, WebRequest request) {
        final var problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problemDetail.setDetail(ex.getMessage());
        // TODO : Log error and pass 'An internal error occurred. Please check the logs for more details or try again
        //  later.'
        return handleExceptionInternal(ex, problemDetail,
                new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}
