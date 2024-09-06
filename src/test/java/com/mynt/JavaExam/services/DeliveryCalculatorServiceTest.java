package com.mynt.JavaExam.services;

import com.mynt.JavaExam.client.CouponClient;
import com.mynt.JavaExam.exception.InvalidInputException;
import com.mynt.JavaExam.model.PriceRate;
import com.mynt.JavaExam.model.dto.CouponResponseDto;
import com.mynt.JavaExam.model.dto.DeliveryPriceResponse;
import com.mynt.JavaExam.model.dto.ParcelRequest;
import com.mynt.JavaExam.repository.PricingRateRepository;
import com.mynt.JavaExam.service.DeliveryCalculatorService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class DeliveryCalculatorServiceTest {

    private DeliveryCalculatorService deliveryCalculatorService;
    @Mock
    private PricingRateRepository pricingRateRepository;
    @Mock
    private CouponClient couponClient;
    private AutoCloseable autoCloseable;

    @BeforeEach
    public void setUp() {
        this.autoCloseable = MockitoAnnotations.openMocks(this);
        List<PriceRate> priceRates = new ArrayList<>();
        priceRates.add(new PriceRate(1L, "HEAVY_PARCEL", 20));
        priceRates.add(new PriceRate(2L, "SMALL_PARCEL", 0.03));
        priceRates.add(new PriceRate(3L, "MEDIUM_PARCEL", 0.04));
        priceRates.add(new PriceRate(4L, "LARGE_PARCEL", 0.05));
        when(pricingRateRepository.findAll()).thenReturn(priceRates);
        this.deliveryCalculatorService = new DeliveryCalculatorService(pricingRateRepository, couponClient);
    }

    @Test
    public void testCalculatePrice_maxWeight() {
        ParcelRequest.Parcel parcel = ParcelRequest.Parcel.builder()
                .weight(51.0)
                .width(10.0)
                .height(10.0)
                .length(10.0)
                .build();
        ParcelRequest parcelRequest = ParcelRequest.builder()
                .parcel(parcel)
                .build();
        Exception exception = assertThrows(InvalidInputException.class, () -> {
            this.deliveryCalculatorService.calculateDelivery(parcelRequest);
        });
        System.out.println(exception.getMessage());
        assertTrue(exception.getMessage().contains("Parcel rejected. Weight exceeds 50.0kg"));
    }

    @Test
    public void testCalculatePrice_largeWeight() {
        ParcelRequest.Parcel parcel = ParcelRequest.Parcel.builder()
                .weight(49.99)
                .width(10.0)
                .height(10.0)
                .length(10.0)
                .build();
        ParcelRequest parcelRequest = ParcelRequest.builder()
                .parcel(parcel)
                .build();
        DeliveryPriceResponse response = this.deliveryCalculatorService.calculateDelivery(parcelRequest);
        assertEquals(response.getDeliveryPrice(), BigDecimal.valueOf(999.80).setScale(2, BigDecimal.ROUND_HALF_UP));
    }

    @Test
    public void testCalculatePrice_heavyParcel() {
        ParcelRequest.Parcel parcel = ParcelRequest.Parcel.builder()
                .weight(49.99)
                .width(10.0)
                .height(10.0)
                .length(10.0)
                .build();
        ParcelRequest parcelRequest = ParcelRequest.builder()
                .parcel(parcel)
                .build();
        DeliveryPriceResponse response = this.deliveryCalculatorService.calculateDelivery(parcelRequest);
        assertEquals(response.getDeliveryPrice(), BigDecimal.valueOf(999.80).setScale(2, BigDecimal.ROUND_HALF_UP));
    }

    @Test
    public void testCalculatePrice_smallParcel() {
        ParcelRequest.Parcel parcel = ParcelRequest.Parcel.builder()
                .weight(9.99)
                .width(10.0)
                .height(10.0)
                .length(10.0)
                .build();
        ParcelRequest parcelRequest = ParcelRequest.builder()
                .parcel(parcel)
                .build();
        DeliveryPriceResponse response = this.deliveryCalculatorService.calculateDelivery(parcelRequest);
        assertEquals(response.getDeliveryPrice(), BigDecimal.valueOf(30.00).setScale(2, BigDecimal.ROUND_HALF_UP));
    }

    @Test
    public void testCalculatePrice_mediumParcel() {
        ParcelRequest.Parcel parcel = ParcelRequest.Parcel.builder()
                .weight(9.99)
                .width(50.3)
                .height(10.5)
                .length(4.3)
                .build();
        ParcelRequest parcelRequest = ParcelRequest.builder()
                .parcel(parcel)
                .build();
        DeliveryPriceResponse response = this.deliveryCalculatorService.calculateDelivery(parcelRequest);
        assertEquals(response.getDeliveryPrice(), BigDecimal.valueOf(90.84).setScale(2, BigDecimal.ROUND_HALF_UP));
    }

    @Test
    public void testCalculatePrice_largeParcel() {
        ParcelRequest.Parcel parcel = ParcelRequest.Parcel.builder()
                .weight(9.99)
                .width(50.2)
                .height(11.5)
                .length(5.2)
                .build();
        ParcelRequest parcelRequest = ParcelRequest.builder()
                .parcel(parcel)
                .build();
        DeliveryPriceResponse response = this.deliveryCalculatorService.calculateDelivery(parcelRequest);

        assertEquals(response.getDeliveryPrice(), BigDecimal.valueOf(150.10).setScale(2, BigDecimal.ROUND_HALF_UP));
    }

    @Test
    public void testCalculatePrice_largeParcel_discount() {
        ParcelRequest.Parcel parcel = ParcelRequest.Parcel.builder()
                .weight(9.99)
                .width(50.2)
                .height(11.5)
                .length(5.2)
                .build();
        ParcelRequest parcelRequest = ParcelRequest.builder()
                .parcel(parcel)
                .voucherCode("MYNT")
                .build();
        LocalDate expiry = LocalDate.now().plusDays(1);
        CouponResponseDto discount = new CouponResponseDto("MYNT", 10.10, expiry);
        when(couponClient.getCouponDiscount("MYNT")).thenReturn(discount);
        DeliveryPriceResponse response = this.deliveryCalculatorService.calculateDelivery(parcelRequest);

        assertEquals(response.getDeliveryPrice(), BigDecimal.valueOf(140.00).setScale(2, BigDecimal.ROUND_HALF_UP));
    }

    @AfterEach
    public void tearDown() throws Exception {
        this.autoCloseable.close();
    }
}
