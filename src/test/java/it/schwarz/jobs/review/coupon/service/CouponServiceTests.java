package it.schwarz.jobs.review.coupon.service;

import java.util.List;
import java.util.Optional;

import it.schwarz.jobs.review.coupon.domain.entity.AmountOfMoney;
import it.schwarz.jobs.review.coupon.domain.entity.ApplicationResult;
import it.schwarz.jobs.review.coupon.domain.entity.Basket;
import it.schwarz.jobs.review.coupon.domain.entity.Coupon;
import it.schwarz.jobs.review.coupon.domain.entity.CouponApplications;
import it.schwarz.jobs.review.coupon.domain.usecase.exception.BasketValueTooLowException;
import it.schwarz.jobs.review.coupon.domain.usecase.exception.CouponAlreadyExistsException;
import it.schwarz.jobs.review.coupon.domain.usecase.exception.CouponCodeNotFoundException;
import it.schwarz.jobs.review.coupon.provider.CouponProvider;
import it.schwarz.jobs.review.coupon.testobjects.TestObjects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class CouponServiceTests
{

    @Mock
    private CouponProvider couponProvider;

    @InjectMocks
    private CouponService couponService;


    private Coupon sampleCoupon;
    private Basket sampleBasket;

    @BeforeEach
    void setUp() {
        sampleCoupon = TestObjects.coupons().COUPON_05_50();
        sampleBasket = new Basket(AmountOfMoney.of("60.00"));
    }

    @Test
    void testFindAllCoupons() {
        List<Coupon> coupons = List.of(TestObjects.coupons().COUPON_12_20(), TestObjects.coupons().COUPON_05_50());
        when(couponProvider.findAll()).thenReturn(coupons);

        var allCoupons = couponService.findAllCoupons();

        assertThat(allCoupons).hasSize(2);
        verify(couponProvider).findAll();
    }

    @Test
    void testReturnEmptyListWhenNoCoupons() {
        when(couponProvider.findAll()).thenReturn(List.of());

        List<Coupon> result = couponService.findAllCoupons();

        assertThat(result).isEmpty();
        verify(couponProvider).findAll();
    }

    @Test
    void testCreateCoupon() {
        // Create
        when(couponProvider.createCoupon(any(Coupon.class))).thenReturn(sampleCoupon);

        var createdCoupon = couponService.createCoupon(sampleCoupon);

        assertThat(createdCoupon).isNotNull();
        assertThat(createdCoupon).isSameAs(sampleCoupon);

    }

    @Test
    void testThrowWhenCreatingDuplicateCoupon() {
        when(couponProvider.createCoupon(sampleCoupon )).thenThrow(new IllegalStateException(sampleCoupon.getCode()));

        assertThatThrownBy(() -> couponService.createCoupon(sampleCoupon))
            .isInstanceOf(CouponAlreadyExistsException.class)
            .hasMessageContaining(sampleCoupon.getCode());
    }


    // ---------- getApplications ----------
    @Test
    void testGetApplicationsSuccess() {

        String couponCode = sampleCoupon.getCode();
        CouponApplications expected = TestObjects.applications().sampleApplicationsForCode(couponCode);
        when(couponProvider.getCouponApplications(couponCode)).thenReturn(Optional.of(expected));

        CouponApplications actual = couponService.getApplications(couponCode);

        assertThat(actual).isEqualTo(expected);
        verify(couponProvider).getCouponApplications(couponCode);
    }

    @Test
    void testGetCouponWithNoApplications() {
        String couponCode = sampleCoupon.getCode();
        CouponApplications expected = new CouponApplications(couponCode,List.of());
        when(couponProvider.getCouponApplications(couponCode)).thenReturn(Optional.of(expected));

        CouponApplications actual = couponService.getApplications(couponCode);

        assertEquals(couponCode, actual.getCouponCode());
        assertEquals(0, actual.getApplicationTimestamps().size());
        assertThat(actual).isEqualTo(expected);
        verify(couponProvider).getCouponApplications(couponCode);
    }

    @Test
    void testGetApplicationsThrowsWhenCouponNotFound() {
        String couponCode = "NON_EXISTENT";
        when(couponProvider.getCouponApplications(couponCode)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> couponService.getApplications(couponCode))
            .isInstanceOf(CouponCodeNotFoundException.class)
            .hasMessageContaining(couponCode);

        verify(couponProvider).getCouponApplications(couponCode);
    }

    // ---------- applyCoupon ----------
    @Test
    void testApplyCouponSuccess() {
        String couponCode = sampleCoupon.getCode();
        when(couponProvider.findById(couponCode)).thenReturn(Optional.of(sampleCoupon));
        doNothing().when(couponProvider).registerCouponApplication(couponCode);

        ApplicationResult result = couponService.applyCoupon(sampleBasket, couponCode);

        assertThat(result.getBasket()).isSameAs(sampleBasket);
        assertThat(result.getAppliedCoupon()).isSameAs(sampleCoupon);
        verify(couponProvider).findById(couponCode);
        verify(couponProvider).registerCouponApplication(couponCode);
    }

    @Test
    void testApplyCouponThrowsWhenCouponNotFound() {
        String couponCode = "NON_EXISTENT";
        when(couponProvider.findById(couponCode)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> couponService.applyCoupon(sampleBasket, couponCode))
            .isInstanceOf(CouponCodeNotFoundException.class)
            .hasMessageContaining(couponCode);

        verify(couponProvider).findById(couponCode);
        verify(couponProvider, never()).registerCouponApplication(any());
    }

    @Test
    void testApplyCouponThrowsWhenBasketLessThanDiscount() {
        String couponCode = sampleCoupon.getCode();
        Basket lowBasket = new Basket(AmountOfMoney.of("3.00")); // less than discount 5.00
        when(couponProvider.findById(couponCode)).thenReturn(Optional.of(sampleCoupon));

        assertThatThrownBy(() -> couponService.applyCoupon(lowBasket, couponCode))
            .isInstanceOf(BasketValueTooLowException.class)
            .hasMessageContaining("less than the discount");

        verify(couponProvider).findById(couponCode);
        verify(couponProvider, never()).registerCouponApplication(any());
    }

    @Test
    void testApplyCouponThrowsWhenBasketLessThanMinBasketValue() {
        String couponCode = sampleCoupon.getCode();
        Basket lowBasket = new Basket(AmountOfMoney.of("40.00")); // less than minBasket 50.00
        when(couponProvider.findById(couponCode)).thenReturn(Optional.of(sampleCoupon));

        assertThatThrownBy(() -> couponService.applyCoupon(lowBasket, couponCode))
            .isInstanceOf(BasketValueTooLowException.class)
            .hasMessageContaining("min. allowed basket value");

        verify(couponProvider).findById(couponCode);
        verify(couponProvider, never()).registerCouponApplication(any());
    }
}