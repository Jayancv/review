package it.schwarz.jobs.review.coupon.config;

import it.schwarz.jobs.review.coupon.provider.CouponProvider;
import it.schwarz.jobs.review.coupon.provider.inmem.InMemoryCouponProvider;
import it.schwarz.jobs.review.coupon.provider.repository.ApplicationJpaRepository;
import it.schwarz.jobs.review.coupon.provider.repository.CouponJpaRepository;
import it.schwarz.jobs.review.coupon.provider.jpa.JpaCouponProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CouponAppConfig {

    @Bean
    public CouponProvider getJpaCouponProvider(
            CouponJpaRepository couponJpaRepository,
            ApplicationJpaRepository applicationRepository) {
        return new JpaCouponProvider(couponJpaRepository, applicationRepository);
    }

    // Comment in/out one of the CouponProvider Beans to select one for runtime
    // @Bean
    public CouponProvider getInMemCouponProvider() {
        return new InMemoryCouponProvider();
    }

//    removed by adding @service annotation
//    @Bean
//    public CouponService getCouponUseCases(CouponProvider couponProvider) {
//        return new CouponService(couponProvider);
//    }
//

}
