package it.schwarz.jobs.review.coupon.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.schwarz.jobs.review.coupon.api.dto.request.ApplyCouponRequestDto;
import it.schwarz.jobs.review.coupon.api.dto.request.CreateCouponRequestDto;
import it.schwarz.jobs.review.coupon.domain.model.AmountOfMoney;
import it.schwarz.jobs.review.coupon.domain.model.ApplicationResult;
import it.schwarz.jobs.review.coupon.domain.model.Basket;
import it.schwarz.jobs.review.coupon.domain.model.Coupon;
import it.schwarz.jobs.review.coupon.domain.exception.BasketValueTooLowException;
import it.schwarz.jobs.review.coupon.domain.exception.CouponCodeNotFoundException;
import it.schwarz.jobs.review.coupon.service.CouponService;
import it.schwarz.jobs.review.coupon.testobjects.TestObjects;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CouponRestController.class)
class CouponRestControllerTests {
    public static final String BASE_PATH = "/api/v1/coupons";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CouponService couponService;


    @Test
    void testReturnEmptyListWhenNoCoupons() throws Exception {
        when(couponService.findAllCoupons()).thenReturn(new ArrayList<>());

        this.mockMvc
                .perform(get(BASE_PATH))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.coupons", hasSize(0)));
    }

    @Test
    void testReturnAllCoupons() throws Exception {
        Coupon coupon1 = new Coupon("C1", AmountOfMoney.of("10"), AmountOfMoney.of("50"), "desc1", 5);
        Coupon coupon2 = new Coupon("C2", AmountOfMoney.of("20"), AmountOfMoney.of("100"), "desc2", 0);
        when(couponService.findAllCoupons()).thenReturn(List.of(coupon1, coupon2));

        this.mockMvc
            .perform(get(BASE_PATH))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.coupons", hasSize(2)))
            .andExpect(jsonPath("$.coupons[0].code").value("C1"))
            .andExpect(jsonPath("$.coupons[0].discount").value(10))
            .andExpect(jsonPath("$.coupons[0].applicationCount").value(5))
            .andExpect(jsonPath("$.coupons[1].code").value("C2"))
            .andExpect(jsonPath("$.coupons[1].applicationCount").value(0));
    }

    @Test
    void testCreateValidCoupon() throws Exception {

        CreateCouponRequestDto request = TestObjects.requests().validCoupon();
        when(couponService.createCoupon(any(Coupon.class))).thenReturn(TestObjects.coupons().COUPON_12_20());

        this.mockMvc
                .perform(post(BASE_PATH)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.coupon.code").value("CODE_12_20"))
                .andExpect(jsonPath("$.coupon.discount").value(12.00))
                .andExpect(jsonPath("$.coupon.minBasketValue").value(20.00))
                .andExpect(jsonPath("$.coupon.description").value("12 for 20"))
                .andExpect(jsonPath("$.coupon.applicationCount").value(0))
                .andReturn();
    }

    @Test
    void testCreateInvalidCoupon() throws Exception {

        CreateCouponRequestDto request = TestObjects.requests().invalidCouponOfNegativeDiscount();

        this.mockMvc
                .perform(post(BASE_PATH)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
    }


    @Test
    void testGetCouponApplications() throws Exception {
        String couponCode = "TEST_05_50";
        when(couponService.getApplications(couponCode)).thenReturn(
            TestObjects.applications().sampleApplicationsForCode(couponCode));

        mockMvc.perform(get(BASE_PATH + "/" + couponCode + "/applications"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.couponCode").value(couponCode))
            .andExpect(jsonPath("$.applicationTimestamps", hasSize(2)));
    }

    @Test
    void testReturnBadRequestWhenGetApplicationsForNonExistentCoupon() throws Exception {
        String couponCode = "NON_EXISTENT";
        when(couponService.getApplications(couponCode))
            .thenThrow(new CouponCodeNotFoundException("Coupon not found"));

        mockMvc.perform(get(BASE_PATH + "/" + couponCode + "/applications"))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.detail").value(containsString("not found")));
    }

    @Test
    void testApplyCouponSuccessfully() throws Exception
    {
        Coupon coupon = TestObjects.coupons().COUPON_12_20();
        ApplyCouponRequestDto request = TestObjects.requests().validApplication();

        Basket basket = new Basket(AmountOfMoney.of(request.basket().value()));
        ApplicationResult result = new ApplicationResult(basket, coupon);
        when(couponService.applyCoupon(any(Basket.class), eq(request.couponCode()))).thenReturn(result);

        mockMvc.perform(post(BASE_PATH + "/applications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.appliedDiscount").value(12.00))
            .andExpect(jsonPath("$.basket.value").value(60.00));
    }

    @Test
    void testReturnBadRequestWhenApplyCouponWithNonExistentCode() throws Exception
    {
        ApplyCouponRequestDto request = TestObjects.requests().invalidApplicationOfNotExistingCode();
        when(couponService.applyCoupon(any(Basket.class), eq(request.couponCode())))
            .thenThrow(new CouponCodeNotFoundException("Coupon not found"));

        mockMvc.perform(post(BASE_PATH + "/applications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.detail").value(containsString("not found")));
    }

    @Test
    void testReturnBadRequestWhenBasketValueTooLow() throws Exception
    {
        ApplyCouponRequestDto request = TestObjects.requests().invalidApplicationOfLowBasketValue();
        when(couponService.applyCoupon(any(Basket.class), eq(request.couponCode())))
            .thenThrow(new BasketValueTooLowException("Basket value too low"));

        mockMvc.perform(post(BASE_PATH + "/applications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.detail").value(containsString("too low")));
    }

}