package it.schwarz.jobs.review.coupon.testobjects;

import java.time.Instant;
import java.util.List;

import it.schwarz.jobs.review.coupon.domain.model.CouponApplications;

public class TestApplications
{
    public CouponApplications sampleApplicationsForCode(String couponCode)
    {
        return new CouponApplications(
            couponCode,
            sampleTimestamps()
        );
    }

    public List<Instant> sampleTimestamps()
    {
        return List.of(
            Instant.now().plusSeconds(1),
            Instant.now().plusSeconds(2)
        );
    }
}
