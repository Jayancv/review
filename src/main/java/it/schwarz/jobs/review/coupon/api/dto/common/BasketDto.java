package it.schwarz.jobs.review.coupon.api.dto.common;

import it.schwarz.jobs.review.coupon.domain.model.AmountOfMoney;
import it.schwarz.jobs.review.coupon.domain.model.Basket;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record BasketDto(
        @NotNull
        @Min(0)
        @Max(10000)
        BigDecimal value
) {
    public Basket toBasket() {
        return new Basket(AmountOfMoney.of(value));
    }

}
