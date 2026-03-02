package it.schwarz.jobs.review.coupon.provider.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.schwarz.jobs.review.coupon.provider.entity.ApplicationJpaEntity;

public interface ApplicationJpaRepository extends JpaRepository<ApplicationJpaEntity, Long> {

    List<ApplicationJpaEntity> findByCouponCode(String couponCode);

}
