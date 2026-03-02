package it.schwarz.jobs.review.coupon.provider.inmem;

import it.schwarz.jobs.review.coupon.domain.entity.AmountOfMoney;
import it.schwarz.jobs.review.coupon.domain.entity.Coupon;
import it.schwarz.jobs.review.coupon.domain.entity.CouponApplications;
import it.schwarz.jobs.review.coupon.provider.CouponProvider;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * InMemory Implementation to simplify local test and development.
 * You can use this if there is no real database available.
 */
public class InMemoryCouponProvider implements CouponProvider {

    private final Map<String, Coupon> coupons = new ConcurrentHashMap<>();
    private final Map<String, List<Instant>> couponApplications = new ConcurrentHashMap<>();

    public InMemoryCouponProvider() {
        // Test Coupons
        Coupon c1 = new Coupon("TEST_05_50", AmountOfMoney.of("5.00"), AmountOfMoney.of("50.00"), "5 for 50");
        Coupon c2 = new Coupon("TEST_15_100", AmountOfMoney.of("15.00"), AmountOfMoney.of("100.00"), "15 for 100");
        Coupon c3 = new Coupon("TEST_40_200", AmountOfMoney.of("40.00"), AmountOfMoney.of("200.00"), "40 for 200");
        coupons.put(c1.getCode(), c1);
        coupons.put(c2.getCode(), c2);
        coupons.put(c3.getCode(), c3);

        // Test DateTimes
        couponApplications.put("TEST_05_50", new CopyOnWriteArrayList<>(List.of(
            Instant.now().plusSeconds(1),
            Instant.now().plusSeconds(2),
            Instant.now().plusSeconds(3),
            Instant.now().plusSeconds(4)
        )));
    }


    @Override
    public Coupon createCoupon(Coupon coupon) {
        // Coupon must not already exist
        var foundCoupon = this.findById(coupon.getCode());
        if (foundCoupon.isPresent()) {
            throw new IllegalStateException("Coupon already exists: " + coupon.getCode());
        }

        coupons.put(coupon.getCode(), coupon);
        return coupon;
    }

    @Override
    public List<Coupon> findAll() {
        return List.copyOf(coupons.values());
    }

    @Override
    public Optional<Coupon> findById(String couponCode) {
        return Optional.ofNullable(coupons.get(couponCode));
    }

    @Override
    public void registerCouponApplication(String couponCode) {
        // Intentionally left blank, because it is currently not used
//        applications.computeIfAbsent(couponCode, k -> new CopyOnWriteArrayList<>())
//            .add(Instant.now());
    }

    @Override
    public Optional<CouponApplications> getCouponApplications(String couponCode) {
        if (!coupons.containsKey(couponCode)) {
            return Optional.empty();
        }
        List<Instant> timestamps = couponApplications.getOrDefault(couponCode, List.of());
        return Optional.of(new CouponApplications(couponCode, timestamps));
    }
}
