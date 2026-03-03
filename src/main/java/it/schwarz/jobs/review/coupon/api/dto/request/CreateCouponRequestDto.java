package it.schwarz.jobs.review.coupon.api.dto.request;

import it.schwarz.jobs.review.coupon.domain.model.AmountOfMoney;
import it.schwarz.jobs.review.coupon.domain.model.Coupon;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateCouponRequestDto(

        @NotNull
        @Size(min = 1, max = 20)
        String code,

        @NotNull
        @Min(0)
        @Max(10000)
        BigDecimal discount,

        @NotNull
        @Min(0)
        @Max(10000)
        BigDecimal minBasketValue,

        @NotNull
        @Size(min = 1, max = 1000)
        String description) {

    public Coupon toCoupon() {
        return new Coupon(
                code,
                AmountOfMoney.of(discount),
                AmountOfMoney.of(minBasketValue),
                description
        );
    }
}
