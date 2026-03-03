package it.schwarz.jobs.review.coupon.domain.model;

import java.time.Instant;
import java.util.List;

public class CouponApplications {
    private final String couponCode;
    private final List<Instant> applicationTimestamps;

    public CouponApplications(String couponCode, List<Instant> applicationDateTime) {
        this.couponCode = couponCode;
        this.applicationTimestamps = applicationDateTime;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public List<Instant> getApplicationTimestamps() {
        return applicationTimestamps;
    }
}
