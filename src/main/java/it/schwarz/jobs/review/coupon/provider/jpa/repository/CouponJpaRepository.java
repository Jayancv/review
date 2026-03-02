package it.schwarz.jobs.review.coupon.provider.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import it.schwarz.jobs.review.coupon.provider.jpa.entity.CouponJpaEntity;

public interface CouponJpaRepository extends JpaRepository<CouponJpaEntity, String> {


}
