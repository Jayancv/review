package it.schwarz.jobs.review.coupon;

import it.schwarz.jobs.review.coupon.api.dto.ErrorResponseDto;
import it.schwarz.jobs.review.coupon.api.dto.request.ApplyCouponRequestDto;
import it.schwarz.jobs.review.coupon.api.dto.request.CreateCouponRequestDto;
import it.schwarz.jobs.review.coupon.api.dto.response.ApplyCouponResponseDto;
import it.schwarz.jobs.review.coupon.api.dto.response.CreateCouponResponseDto;
import it.schwarz.jobs.review.coupon.api.dto.response.GetCouponApplicationsResponseDto;
import it.schwarz.jobs.review.coupon.api.dto.response.GetCouponsResponseDto;
import it.schwarz.jobs.review.coupon.provider.CouponProvider;
import it.schwarz.jobs.review.coupon.testobjects.TestObjects;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "coupon.provider=jpa",
        "coupon.init-data=false"
    })
@ActiveProfiles(profiles = "dev")
public class CouponAppIT {


    @LocalServerPort
    private int port;

    @Autowired
    private CouponProvider couponProvider;

    @Autowired
    private TestRestTemplate restTemplate;

    private static final String HOST_URL = "http://localhost:";
    private static final String API_BASE = "/api/v1/coupons";

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = HOST_URL + port + API_BASE;
        couponProvider.reset();
    }


    // ─────────────────────────────────────────
    // GET /api/v1/coupons
    // ─────────────────────────────────────────

    @Test
    void getCoupons_returnsEmptyList_whenNoCouponsExist() {
        GetCouponsResponseDto response = restTemplate
            .getForObject(baseUrl, GetCouponsResponseDto.class);

        assertThat(response).isNotNull();
        assertThat(response.coupons()).isEmpty();
    }

    @Test
    void testGetCouponOverview() {
        // Arrange - create known state explicitly
        restTemplate.postForObject(baseUrl, TestObjects.requests().validCoupon(),
            CreateCouponResponseDto.class);

        // Act
        GetCouponsResponseDto response = restTemplate
            .getForObject(baseUrl, GetCouponsResponseDto.class);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.coupons()).hasSize(1);
        assertThat(response.coupons().getFirst().code())
            .isEqualTo(TestObjects.requests().validCoupon().code());
    }

    @Test
    void getCoupons_reflectsApplicationCount() {
        // Arrange
        restTemplate.postForObject(baseUrl, TestObjects.requests().validCoupon(),
            CreateCouponResponseDto.class);
        restTemplate.postForEntity(baseUrl + "/applications",
            TestObjects.requests().validApplication(), ApplyCouponResponseDto.class);

        // Act
        GetCouponsResponseDto response = restTemplate
            .getForObject(baseUrl, GetCouponsResponseDto.class);

        // Assert
        assertThat(response.coupons().getFirst().applicationCount()).isEqualTo(1);
    }

    // ─────────────────────────────────────────
    // POST /api/v1/coupons
    // ─────────────────────────────────────────

    @Test
    void createCoupon_returnsCreatedCoupon_whenRequestIsValid() {
        CreateCouponRequestDto request = TestObjects.requests().validCoupon();

        ResponseEntity<CreateCouponResponseDto> response = restTemplate
            .postForEntity(baseUrl, request, CreateCouponResponseDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().coupon().code()).isEqualTo(request.code());
        assertThat(response.getBody().coupon().discount())
            .isEqualByComparingTo(request.discount());
        assertThat(response.getBody().coupon().minBasketValue())
            .isEqualByComparingTo(request.minBasketValue());
        assertThat(response.getBody().coupon().applicationCount()).isZero();
    }

    @Test
    void createCoupon_returnsConflict_whenCouponAlreadyExists() {
        // Arrange - create it once first
        restTemplate.postForEntity(baseUrl, TestObjects.requests().validCoupon(),
            CreateCouponResponseDto.class);

        // Act - try to create it again
        ResponseEntity<ErrorResponseDto> response = restTemplate
            .postForEntity(baseUrl, TestObjects.requests().validCoupon(),
                ErrorResponseDto.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);  // 409
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().detail())
            .contains(TestObjects.requests().validCoupon().code());
    }

    @Test
    void createCoupon_returnsBadRequest_whenDiscountIsNegative() {
        ResponseEntity<ErrorResponseDto> response = restTemplate
            .postForEntity(baseUrl,
                TestObjects.requests().invalidCouponOfNegativeDiscount(),
                ErrorResponseDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void createCoupon_returnsBadRequest_whenCodeIsBlank() {
        CreateCouponRequestDto request = new CreateCouponRequestDto(
            "", new BigDecimal("10.00"), new BigDecimal("50.00"), "desc");

        ResponseEntity<ErrorResponseDto> response = restTemplate
            .postForEntity(baseUrl, request, ErrorResponseDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void createCoupon_returnsBadRequest_whenDescriptionIsBlank() {
        CreateCouponRequestDto request = new CreateCouponRequestDto(
            "CODE_X", new BigDecimal("10.00"), new BigDecimal("50.00"), "");

        ResponseEntity<ErrorResponseDto> response = restTemplate
            .postForEntity(baseUrl, request, ErrorResponseDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    // ─────────────────────────────────────────
    // POST /api/v1/coupons/applications
    // ─────────────────────────────────────────

    @Test
    void applyCoupon_returnsDiscountedResult_whenRequestIsValid() {
        // Arrange - coupon must exist first
        restTemplate.postForObject(baseUrl, TestObjects.requests().validCoupon(),
            CreateCouponResponseDto.class);

        // Act
        ResponseEntity<ApplyCouponResponseDto> response = restTemplate
            .postForEntity(baseUrl + "/applications",
                TestObjects.requests().validApplication(),
                ApplyCouponResponseDto.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().appliedDiscount()).isGreaterThan(BigDecimal.ZERO);
        assertThat(response.getBody().basket().value())
            .isEqualByComparingTo(new BigDecimal("60.00"));
    }

    @Test
    void applyCoupon_returnsNotFound_whenCouponCodeDoesNotExist() {
        ResponseEntity<ErrorResponseDto> response = restTemplate
            .postForEntity(baseUrl + "/applications",
                TestObjects.requests().invalidApplicationOfNotExistingCode(),
                ErrorResponseDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);  // 404
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().detail()).contains("not found");
    }

    @Test
    void applyCoupon_returnsUnprocessable_whenBasketValueTooLow() {
        // Arrange
        restTemplate.postForObject(baseUrl, TestObjects.requests().validCoupon(),
            CreateCouponResponseDto.class);

        // Act
        ResponseEntity<ErrorResponseDto> response = restTemplate
            .postForEntity(baseUrl + "/applications",
                TestObjects.requests().invalidApplicationOfLowBasketValue(),
                ErrorResponseDto.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST); // 422
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().detail()).contains("basket value");
    }

    @Test
    void applyCoupon_returnsBadRequest_whenBasketIsNull() {
        ApplyCouponRequestDto request = new ApplyCouponRequestDto(null, "CODE_12_20");

        ResponseEntity<ErrorResponseDto> response = restTemplate
            .postForEntity(baseUrl + "/applications", request, ErrorResponseDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    // ─────────────────────────────────────────
    // GET /api/v1/coupons/{code}/applications
    // ─────────────────────────────────────────

    @Test
    void getCouponApplications_returnsTimestamps_whenCouponHasApplications() {
        // Arrange - create coupon and apply it
        restTemplate.postForObject(baseUrl, TestObjects.requests().validCoupon(),
            CreateCouponResponseDto.class);
        restTemplate.postForEntity(baseUrl + "/applications",
            TestObjects.requests().validApplication(), ApplyCouponResponseDto.class);
        restTemplate.postForEntity(baseUrl + "/applications",
            TestObjects.requests().validApplication(), ApplyCouponResponseDto.class);

        // Act
        ResponseEntity<GetCouponApplicationsResponseDto> response = restTemplate
            .getForEntity(baseUrl + "/" + TestObjects.requests().validCoupon().code()
                + "/applications", GetCouponApplicationsResponseDto.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().couponCode())
            .isEqualTo(TestObjects.requests().validCoupon().code());
        assertThat(response.getBody().applicationTimestamps()).hasSize(2);
    }

    @Test
    void getCouponApplications_returnsEmptyList_whenCouponHasNoApplications() {
        // Arrange - create coupon but never apply it
        restTemplate.postForObject(baseUrl, TestObjects.requests().validCoupon(),
            CreateCouponResponseDto.class);

        // Act
        ResponseEntity<GetCouponApplicationsResponseDto> response = restTemplate
            .getForEntity(baseUrl + "/" + TestObjects.requests().validCoupon().code()
                + "/applications", GetCouponApplicationsResponseDto.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().applicationTimestamps()).isEmpty();
    }

    @Test
    void getCouponApplications_returnsNotFound_whenCouponDoesNotExist() {
        String nonExistingCode = TestObjects.coupons().NOT_EXISTING_COUPON().getCode();

        ResponseEntity<ErrorResponseDto> response = restTemplate
            .getForEntity(baseUrl + "/" + nonExistingCode + "/applications",
                ErrorResponseDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);  // 404
    }

}


