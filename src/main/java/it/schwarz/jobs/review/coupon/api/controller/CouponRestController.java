package it.schwarz.jobs.review.coupon.api.controller;

import it.schwarz.jobs.review.coupon.api.dto.request.ApplyCouponRequestDto;
import it.schwarz.jobs.review.coupon.api.dto.request.CreateCouponRequestDto;
import it.schwarz.jobs.review.coupon.api.dto.response.ApplyCouponResponseDto;
import it.schwarz.jobs.review.coupon.api.dto.response.CreateCouponResponseDto;
import it.schwarz.jobs.review.coupon.api.dto.response.GetCouponApplicationsResponseDto;
import it.schwarz.jobs.review.coupon.api.dto.response.GetCouponsResponseDto;
import it.schwarz.jobs.review.coupon.service.CouponService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Add @Validated
@RestController
@RequestMapping(path = "/api/v1/coupons")   // API versioning
public class CouponRestController {

    private final CouponService couponService;


    public CouponRestController(CouponService couponService) {
        this.couponService = couponService;
    }


    @GetMapping()  // Can paginate
    public ResponseEntity<GetCouponsResponseDto> getCoupons() {
        var coupons = couponService.findAllCoupons();

        // Map from Domain to API
        var response = GetCouponsResponseDto.of(coupons);

        return ResponseEntity.ok(response);
    }


    @PostMapping()
    public ResponseEntity<CreateCouponResponseDto> createCoupon(@Valid @RequestBody CreateCouponRequestDto request) {

        // Map from API to Domain
        var coupon = request.toCoupon();

        var couponCreated = couponService.createCoupon(coupon);

        // Map from Domain to API and return
        var response = CreateCouponResponseDto.of(couponCreated);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // @NotBlank(message = "Coupon code must not be blank")
    @GetMapping("/{couponCode}/applications")
    public ResponseEntity<GetCouponApplicationsResponseDto> getCouponApplications(@PathVariable("couponCode") String couponCode) {
        var couponApplications = couponService.getApplications(couponCode);

        // Map from Domain to API
        var response = GetCouponApplicationsResponseDto.of(couponApplications);

        return ResponseEntity.ok(response);
    }


    @PostMapping("/applications")  // can add coupon no as path parameter
    public ResponseEntity<ApplyCouponResponseDto> applyCoupon(@Valid @RequestBody ApplyCouponRequestDto request) {

        // Map from API to Domain
        var basket = request.basket().toBasket();
        var couponCode = request.couponCode();

        var applicationResult = couponService.applyCoupon(basket, couponCode);

        // Map from Domain to API and return
        var response = ApplyCouponResponseDto.of(applicationResult);
        return ResponseEntity.ok(response);
    }

}
