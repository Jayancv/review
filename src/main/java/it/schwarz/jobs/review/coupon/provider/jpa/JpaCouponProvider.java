package it.schwarz.jobs.review.coupon.provider.jpa;

import it.schwarz.jobs.review.coupon.domain.model.AmountOfMoney;
import it.schwarz.jobs.review.coupon.domain.model.Coupon;
import it.schwarz.jobs.review.coupon.domain.model.CouponApplications;
import it.schwarz.jobs.review.coupon.provider.CouponProvider;
import it.schwarz.jobs.review.coupon.provider.jpa.entity.ApplicationJpaEntity;
import it.schwarz.jobs.review.coupon.provider.jpa.entity.CouponJpaEntity;
import it.schwarz.jobs.review.coupon.provider.jpa.repository.ApplicationJpaRepository;
import it.schwarz.jobs.review.coupon.provider.jpa.repository.CouponJpaRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class JpaCouponProvider implements CouponProvider {

    private static final Logger log = LoggerFactory.getLogger(JpaCouponProvider.class);

    private final CouponJpaRepository couponJpaRepository;
    private final ApplicationJpaRepository applicationRepository;

    public JpaCouponProvider(CouponJpaRepository couponJpaRepository, ApplicationJpaRepository applicationRepository) {
        this.couponJpaRepository = couponJpaRepository;
        this.applicationRepository = applicationRepository;
        log.info("JPA Provider initialized.");
    }

    @Override
    public Coupon createCoupon(Coupon coupon) {
        if (couponJpaRepository.existsById(coupon.getCode())) {
            log.debug("Coupon already exists in DB code={}", coupon.getCode());
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
        CouponJpaEntity coupon = couponJpaRepository.findById(couponCode)
            .orElseThrow(() -> new IllegalStateException("Coupon not exists: " + couponCode));

        applicationRepository.save(new ApplicationJpaEntity(coupon, Instant.now()));
    }

    @Override
    public Optional<Coupon> findById(String couponCode) {
        var found = couponJpaRepository.findById(couponCode);
        return found.map(this::jpaToDomain);
    }

    @Override
    public Optional<CouponApplications> getCouponApplications(String couponCode) {
        var found = couponJpaRepository.findById(couponCode);
        return found.map(couponJpaEntity -> {
            List<Instant> timestamps = Optional.ofNullable( couponJpaEntity.getApplications())
                .orElse(List.of())
                .stream()
                .map(ApplicationJpaEntity::getTimestamp)
                .toList();
            return new CouponApplications(couponJpaEntity.getCode(), timestamps);
        });
    }

    @Override
    public void reset()
    {
        log.warn("Resetting all coupon data - this should only happen in non-production");
        applicationRepository.deleteAll();
        couponJpaRepository.deleteAll();
        log.warn("All coupon data deleted");
    }

    private CouponJpaEntity domainToJpa(Coupon coupon) {
        return new CouponJpaEntity(
            coupon.getCode(),
            coupon.getDiscount().toBigDecimal(),
            coupon.getMinBasketValue().toBigDecimal(),
            coupon.getDescription()
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
