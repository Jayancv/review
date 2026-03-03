package it.schwarz.jobs.review.coupon.provider.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import it.schwarz.jobs.review.coupon.provider.jpa.entity.ApplicationJpaEntity;

public interface ApplicationJpaRepository extends JpaRepository<ApplicationJpaEntity, Long> {

}
