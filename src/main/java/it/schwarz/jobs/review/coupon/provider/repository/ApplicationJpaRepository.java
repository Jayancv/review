package it.schwarz.jobs.review.coupon.provider.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import it.schwarz.jobs.review.coupon.provider.entity.ApplicationJpaEntity;

public interface ApplicationJpaRepository extends JpaRepository<ApplicationJpaEntity, Long> {


}
