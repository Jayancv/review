package it.schwarz.jobs.review.coupon.provider.jpa;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import it.schwarz.jobs.review.coupon.domain.model.Coupon;
import it.schwarz.jobs.review.coupon.domain.model.CouponApplications;
import it.schwarz.jobs.review.coupon.provider.CouponProvider;
import it.schwarz.jobs.review.coupon.testobjects.TestObjects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import(JpaCouponProvider.class)
@TestPropertySource(properties = "spring.sql.init.mode=never")
class JpaCouponProviderTest
{
    @Autowired
    private CouponProvider couponProvider;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        // Clear any leftover data
        couponProvider.reset();
    }

    @Test
    void shouldCreateCouponSuccessfully() {
        Coupon coupon = TestObjects.coupons().COUPON_12_20();

        Coupon created = couponProvider.createCoupon(coupon);

        assertThat(created.getCode()).isEqualTo(coupon.getCode());
        assertThat(created.getDiscount()).isEqualTo(coupon.getDiscount());
        assertThat(created.getMinBasketValue()).isEqualTo(coupon.getMinBasketValue());
        assertThat(created.getDescription()).isEqualTo(coupon.getDescription());
        assertThat(created.getApplicationCount()).isZero();

        // Verify it's persisted
        Optional<Coupon> found = couponProvider.findById(coupon.getCode());
        assertThat(found).isPresent();
        assertThat(found.get().getCode()).isEqualTo(coupon.getCode());
    }

    @Test
    void shouldThrowExceptionWhenCreatingDuplicateCoupon() {
        Coupon coupon = TestObjects.coupons().COUPON_12_20();
        couponProvider.createCoupon(coupon);

        assertThatThrownBy(() -> couponProvider.createCoupon(coupon))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("already exists");
    }

    @Test
    void shouldFindAllCoupons() {
        Coupon c1 = TestObjects.coupons().COUPON_12_20();
        Coupon c2 = TestObjects.coupons().COUPON_05_50();
        couponProvider.createCoupon(c1);
        couponProvider.createCoupon(c2);

        List<Coupon> all = couponProvider.findAll();

        assertThat(all).hasSize(2).extracting(Coupon::getCode)
            .containsExactlyInAnyOrder(c1.getCode(), c2.getCode());
    }

    @Test
    void shouldReturnEmptyListWhenNoCoupons() {
        List<Coupon> all = couponProvider.findAll();
        assertThat(all).isEmpty();
    }

    @Test
    void shouldFindByIdForExistingCoupon() {
        Coupon coupon = TestObjects.coupons().COUPON_12_20();
        couponProvider.createCoupon(coupon);

        Optional<Coupon> found = couponProvider.findById(coupon.getCode());

        assertThat(found).isPresent();
        assertThat(found.get().getCode()).isEqualTo(coupon.getCode());
    }

    @Test
    void shouldReturnEmptyOptionalWhenFindByIdForNonExistingCoupon() {
        Optional<Coupon> found = couponProvider.findById("NON_EXISTENT");
        assertThat(found).isEmpty();
    }

    @Test
    void shouldRegisterCouponApplication() {
        Coupon coupon = TestObjects.coupons().COUPON_12_20();
        couponProvider.createCoupon(coupon);

        couponProvider.registerCouponApplication(coupon.getCode());

        entityManager.flush();
        entityManager.clear();

        Optional<CouponApplications> apps = couponProvider.getCouponApplications(coupon.getCode());
        assertThat(apps).isPresent();
        assertThat(apps.get().getApplicationTimestamps()).hasSize(1);
    }

    @Test
    void shouldRegisterMultipleApplications() {
        Coupon coupon = TestObjects.coupons().COUPON_12_20();
        couponProvider.createCoupon(coupon);

        couponProvider.registerCouponApplication(coupon.getCode());
        couponProvider.registerCouponApplication(coupon.getCode());

        entityManager.flush();
        entityManager.clear();

        Optional<CouponApplications> apps = couponProvider.getCouponApplications(coupon.getCode());
        assertThat(apps).isPresent();
        assertThat(apps.get().getApplicationTimestamps()).hasSize(2);
    }

    @Test
    void shouldGetCouponApplicationsForExistingCoupon() {
        Coupon coupon = TestObjects.coupons().COUPON_12_20();
        couponProvider.createCoupon(coupon);
        couponProvider.registerCouponApplication(coupon.getCode());

        entityManager.flush();
        entityManager.clear();

        Optional<CouponApplications> apps = couponProvider.getCouponApplications(coupon.getCode());

        assertThat(apps).isPresent();
        assertThat(apps.get().getCouponCode()).isEqualTo(coupon.getCode());
        assertThat(apps.get().getApplicationTimestamps()).isNotEmpty();
    }

    @Test
    void shouldReturnEmptyOptionalWhenGetCouponApplicationsForNonExistingCoupon() {
        Optional<CouponApplications> apps = couponProvider.getCouponApplications("NON_EXISTENT");
        assertThat(apps).isEmpty();
    }

    @Test
    void shouldReturnEmptyApplicationsListForCouponWithNoApplications() {
        Coupon coupon = TestObjects.coupons().COUPON_12_20();
        couponProvider.createCoupon(coupon);

        Optional<CouponApplications> apps = couponProvider.getCouponApplications(coupon.getCode());

        assertThat(apps).isPresent();
        assertThat(apps.get().getApplicationTimestamps()).isEmpty();
    }

    // Missing: applicationCount reflects registered applications in findAll/findById
    @Test
    void shouldReflectApplicationCountInFindAll() {
        Coupon coupon = TestObjects.coupons().COUPON_12_20();
        couponProvider.createCoupon(coupon);
        couponProvider.registerCouponApplication(coupon.getCode());
        couponProvider.registerCouponApplication(coupon.getCode());

        entityManager.flush();
        entityManager.clear();

        List<Coupon> all = couponProvider.findAll();
        assertThat(all.getFirst().getApplicationCount()).isEqualTo(2);
    }

    // Missing: reset clears both coupons and applications
    @Test
    void shouldResetClearAllData() {
        couponProvider.createCoupon(TestObjects.coupons().COUPON_12_20());
        couponProvider.registerCouponApplication(TestObjects.coupons().COUPON_12_20().getCode());

        couponProvider.reset();

        assertThat(couponProvider.findAll()).isEmpty();
        assertThat(couponProvider.getCouponApplications(
            TestObjects.coupons().COUPON_12_20().getCode())).isEmpty();
    }
}