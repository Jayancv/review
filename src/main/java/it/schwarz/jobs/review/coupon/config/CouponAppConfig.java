package it.schwarz.jobs.review.coupon.config;

import it.schwarz.jobs.review.coupon.provider.CouponProvider;
import it.schwarz.jobs.review.coupon.provider.inmem.InMemoryCouponProvider;
import it.schwarz.jobs.review.coupon.provider.jpa.repository.ApplicationJpaRepository;
import it.schwarz.jobs.review.coupon.provider.jpa.repository.CouponJpaRepository;
import it.schwarz.jobs.review.coupon.provider.jpa.JpaCouponProvider;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class CouponAppConfig {

    @Bean
    @ConditionalOnProperty(name = "coupon.provider", havingValue = "jpa", matchIfMissing = true)
    public CouponProvider getJpaCouponProvider(
            CouponJpaRepository couponJpaRepository,
            ApplicationJpaRepository applicationRepository) {
        return new JpaCouponProvider(couponJpaRepository, applicationRepository);
    }

    // Comment in/out one of the CouponProvider Beans to select one for runtime
     @Bean
     @ConditionalOnProperty(name = "coupon.provider", havingValue = "inmem", matchIfMissing = false)
     @Profile("dev")
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
