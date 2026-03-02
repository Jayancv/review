package it.schwarz.jobs.review.coupon.provider.jpa;

import it.schwarz.jobs.review.coupon.domain.entity.AmountOfMoney;
import it.schwarz.jobs.review.coupon.domain.entity.Coupon;
import it.schwarz.jobs.review.coupon.domain.entity.CouponApplications;
import it.schwarz.jobs.review.coupon.provider.CouponProvider;
import it.schwarz.jobs.review.coupon.provider.entity.ApplicationJpaEntity;
import it.schwarz.jobs.review.coupon.provider.entity.CouponJpaEntity;
import it.schwarz.jobs.review.coupon.provider.repository.ApplicationJpaRepository;
import it.schwarz.jobs.review.coupon.provider.repository.CouponJpaRepository;

import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class JpaCouponProvider implements CouponProvider {

    private final CouponJpaRepository couponJpaRepository;
    private final ApplicationJpaRepository applicationRepository;

    public JpaCouponProvider(CouponJpaRepository couponJpaRepository, ApplicationJpaRepository applicationRepository) {
        this.couponJpaRepository = couponJpaRepository;
        this.applicationRepository = applicationRepository;
    }

    @Override
    @Transactional
    public Coupon createCoupon(Coupon coupon) {
        if (couponJpaRepository.existsById(coupon.getCode())) {
            throw new IllegalStateException("Coupon already exists: " + coupon.getCode());
        }
        var toPersist = domainToJpa(coupon);
        var persisted = couponJpaRepository.save(toPersist);
        return jpaToDomain(persisted);
    }

    @Override
    public List<Coupon> findAll() {
        return couponJpaRepository.findAll().stream()
                .map(this::jpaToDomain)
                .toList();
    }

    @Override
    public void registerCouponApplication(String couponCode) {
        applicationRepository.save(new ApplicationJpaEntity(
                couponCode,
                Instant.now()));
    }

    @Override
    public Optional<Coupon> findById(String couponCode) {
        var found = couponJpaRepository.findById(couponCode);
        return found.map(this::jpaToDomain);
    }

    @Override
    public Optional<CouponApplications> getCouponApplications(String couponCode) {
        var found = couponJpaRepository.findById(couponCode);  // Check do i need to check is found optional check
        return found.map(couponJpaEntity -> new CouponApplications(
                couponJpaEntity.getCode(),
                couponJpaEntity.getApplications().stream()
                        .map(ApplicationJpaEntity::getTimestamp)
                        .toList()));
    }

    private CouponJpaEntity domainToJpa(Coupon coupon) {
        return new CouponJpaEntity(
                coupon.getCode(),
                coupon.getDiscount().toBigDecimal(),
                coupon.getDescription(),
                coupon.getMinBasketValue().toBigDecimal()
        );
    }

    private Coupon jpaToDomain(CouponJpaEntity couponJpaEntity) {

        return new Coupon(
                couponJpaEntity.getCode(),
                AmountOfMoney.of(couponJpaEntity.getDiscount()),
                AmountOfMoney.of(couponJpaEntity.getMinBasketValue()),
                couponJpaEntity.getDescription(),
                couponJpaEntity.getApplications() == null ? 0 : couponJpaEntity.getApplications().size()
        );
    }

}
