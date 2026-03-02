package it.schwarz.jobs.review.coupon.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

public class AmountOfMoney {
    public static final AmountOfMoney ZERO = new AmountOfMoney(BigDecimal.ZERO);
    private final BigDecimal amount;

    private AmountOfMoney(BigDecimal amount) {
        this.amount = amount;
    }

    public static AmountOfMoney of(String amountAsString) {
        return new AmountOfMoney(new BigDecimal(amountAsString));
    }

    public static AmountOfMoney of(BigDecimal amountAsBigDecimal) {
        return new AmountOfMoney(amountAsBigDecimal);
    }

    public boolean isGreaterThan(AmountOfMoney otherAmount) {
        return (amount.compareTo(otherAmount.amount) > 0);
    }

    public boolean isLessThan(AmountOfMoney otherAmount) {
        return (amount.compareTo(otherAmount.amount) < 0);
    }

    public BigDecimal toBigDecimal() {
        return amount;
    }

    @Override
    public boolean equals(Object o)
    {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AmountOfMoney that = (AmountOfMoney) o;
        return Objects.equals(amount, that.amount);
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(amount);
    }
}
