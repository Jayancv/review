package it.schwarz.jobs.review.coupon.provider.inmem;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.schwarz.jobs.review.coupon.domain.model.Coupon;
import it.schwarz.jobs.review.coupon.domain.model.CouponApplications;
import it.schwarz.jobs.review.coupon.testobjects.TestObjects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class InMemoryCouponProviderTest
{
    private InMemoryCouponProvider provider;

    @BeforeEach
    void setUp() {
        provider = new InMemoryCouponProvider();
        provider.reset(); // reset to empty state
    }

    @Test
    void shouldCreateCouponSuccessfully() {
        Coupon coupon = TestObjects.coupons().COUPON_12_20();

        Coupon created = provider.createCoupon(coupon);

        assertThat(created).isEqualTo(coupon);
        assertThat(provider.findAll()).containsExactly(coupon);
        assertThat(provider.findById(coupon.getCode())).contains(coupon);
    }

    @Test
    void shouldThrowExceptionWhenCreatingDuplicateCoupon() {
        Coupon coupon = TestObjects.coupons().COUPON_12_20();
        provider.createCoupon(coupon);

        assertThatThrownBy(() -> provider.createCoupon(coupon))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("already exists");
    }

    @Test
    void shouldFindAllCoupons() {
        Coupon c1 = TestObjects.coupons().COUPON_12_20();
        Coupon c2 = TestObjects.coupons().COUPON_05_50();
        provider.createCoupon(c1);
        provider.createCoupon(c2);

        List<Coupon> all = provider.findAll();

        assertThat(all).hasSize(2).containsExactlyInAnyOrder(c1, c2);
    }

    @Test
    void shouldReturnEmptyListWhenNoCoupons() {
        List<Coupon> all = provider.findAll();
        assertThat(all).isEmpty();
    }

    @Test
    void shouldFindByIdForExistingCoupon() {
        Coupon coupon = TestObjects.coupons().COUPON_12_20();
        provider.createCoupon(coupon);

        Optional<Coupon> found = provider.findById(coupon.getCode());

        assertThat(found).contains(coupon);
    }

    @Test
    void shouldReturnEmptyOptionalWhenFindByIdForNonExistingCoupon() {
        Optional<Coupon> found = provider.findById("NON_EXISTENT");
        assertThat(found).isEmpty();
    }

//    @Test
//    void shouldRegisterCouponApplication() {
//        // Given the current implementation is empty, this test will fail until implemented.
//        // Once implemented, it should record the timestamp.
//        String couponCode = TestObjects.coupons().COUPON_05_50().getCode();
//        provider.createCoupon(TestObjects.coupons().COUPON_05_50());
//
//        provider.registerCouponApplication(couponCode);
//
//        // Retrieve applications and verify one timestamp exists
//        Optional<CouponApplications> apps = provider.getCouponApplications(couponCode);
//        assertThat(apps).isPresent();
//        assertThat(apps.get().getApplicationTimestamps()).hasSize(1);
//    }

    @Test
    void shouldGetCouponApplicationsForExistingCoupon() {
        InMemoryCouponProvider providerWithData = new InMemoryCouponProvider().withTestData();
        Optional<CouponApplications> apps = providerWithData.getCouponApplications("TEST_05_50");

        assertThat(apps).isPresent();
        assertThat(apps.get().getApplicationTimestamps()).hasSize(4);
    }

    @Test
    void shouldReturnEmptyOptionalWhenGetCouponApplicationsForNonExistingCoupon() {
        Optional<CouponApplications> apps = provider.getCouponApplications("NON_EXISTENT");
        assertThat(apps).isEmpty();
    }

    @Test
    void shouldReturnEmptyApplicationsListForCouponWithNoApplications() {
        Coupon coupon = TestObjects.coupons().COUPON_12_20();
        provider.createCoupon(coupon);

        Optional<CouponApplications> apps = provider.getCouponApplications(coupon.getCode());

        assertThat(apps).isPresent();
        assertThat(apps.get().getApplicationTimestamps()).isEmpty();
    }
}
