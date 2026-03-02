package it.schwarz.jobs.review.coupon.domain.model;

public class Basket {
    private final AmountOfMoney value;

    public Basket(AmountOfMoney value) {
        this.value = value;
    }

    public AmountOfMoney getValue() {
        return value;
    }
}
