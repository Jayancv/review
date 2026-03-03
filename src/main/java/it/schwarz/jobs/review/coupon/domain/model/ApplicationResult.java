package it.schwarz.jobs.review.coupon.domain.model;

public class ApplicationResult {
    private final Basket basket;
    private final Coupon appliedCoupon;

    public ApplicationResult(Basket basket, Coupon appliedCoupon) {
        this.basket = basket;
        this.appliedCoupon = appliedCoupon;
    }

    public Basket getBasket() {
        return basket;
    }

    public Coupon getAppliedCoupon() {
        return appliedCoupon;
    }
}
