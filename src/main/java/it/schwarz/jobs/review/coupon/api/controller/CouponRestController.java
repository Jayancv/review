package it.schwarz.jobs.review.coupon.api.controller;

import it.schwarz.jobs.review.coupon.api.dto.request.ApplyCouponRequestDto;
import it.schwarz.jobs.review.coupon.api.dto.request.CreateCouponRequestDto;
import it.schwarz.jobs.review.coupon.api.dto.response.ApplyCouponResponseDto;
import it.schwarz.jobs.review.coupon.api.dto.response.CreateCouponResponseDto;
import it.schwarz.jobs.review.coupon.api.dto.response.GetCouponApplicationsResponseDto;
import it.schwarz.jobs.review.coupon.api.dto.response.GetCouponsResponseDto;
import it.schwarz.jobs.review.coupon.service.CouponService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Add @Validated
@RestController
@RequestMapping(path = "/api/v1/coupons")   // API versioning
public class CouponRestController {

    private static final Logger log = LoggerFactory.getLogger(CouponRestController.class);

    public static final String PATH = "/api/v1/coupons";

    private final CouponService couponService;

    public CouponRestController(CouponService couponService) {
        this.couponService = couponService;
    }


    @GetMapping()  // Can paginate
    public ResponseEntity<GetCouponsResponseDto> getCoupons() {
        log.debug("REST GET {} - fetching all coupons", PATH);

        var coupons = couponService.findAllCoupons();

        // Map from Domain to API
        var response = GetCouponsResponseDto.of(coupons);

        log.debug("REST GET {} - returning {} coupons", PATH, coupons.size());
        return ResponseEntity.ok(response);
    }


    @PostMapping()
    public ResponseEntity<CreateCouponResponseDto> createCoupon(@Valid @RequestBody CreateCouponRequestDto request) {
        log.info("REST POST {} - creating coupon with code={}", PATH, request.code());
        // Map from API to Domain
        var coupon = request.toCoupon();

        var couponCreated = couponService.createCoupon(coupon);

        // Map from Domain to API and return
        var response = CreateCouponResponseDto.of(couponCreated);
        log.info("REST POST {} - coupon created successfully code={}", PATH, couponCreated.getCode());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // @NotBlank(message = "Coupon code must not be blank")
    @GetMapping("/{couponCode}/applications")
    public ResponseEntity<GetCouponApplicationsResponseDto> getCouponApplications(@PathVariable("couponCode") @NotBlank String couponCode) {
        log.debug("REST GET {}/{}/applications - fetching applications", PATH, couponCode);
        var couponApplications = couponService.getApplications(couponCode);

        // Map from Domain to API
        var response = GetCouponApplicationsResponseDto.of(couponApplications);
        log.debug("REST GET {}/{}/applications - found {} applications", PATH, couponCode,
            couponApplications.getApplicationTimestamps().size());
        return ResponseEntity.ok(response);
    }


    @PostMapping("/applications")  // can add coupon no as path parameter
    public ResponseEntity<ApplyCouponResponseDto> applyCoupon(@Valid @RequestBody ApplyCouponRequestDto request) {
        log.info("REST POST {}/applications - applying couponCode={} basketValue={}", PATH,
            request.couponCode(), request.basket().value());
        // Map from API to Domain
        var basket = request.basket().toBasket();
        var couponCode = request.couponCode();

        var applicationResult = couponService.applyCoupon(basket, couponCode);

        // Map from Domain to API and return
        var response = ApplyCouponResponseDto.of(applicationResult);
        log.info("REST POST {}/applications - coupon applied successfully couponCode={} discount={}", PATH,
            request.couponCode(),
            applicationResult.getAppliedCoupon().getDiscount().toBigDecimal());
        return ResponseEntity.ok(response);
    }

}
