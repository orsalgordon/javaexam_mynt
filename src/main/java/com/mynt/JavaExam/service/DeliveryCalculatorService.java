package com.mynt.JavaExam.service;

import com.mynt.JavaExam.client.CouponClient;
import com.mynt.JavaExam.exception.InvalidInputException;
import com.mynt.JavaExam.model.PriceRate;
import com.mynt.JavaExam.model.dto.CouponResponseDto;
import com.mynt.JavaExam.model.dto.DeliveryPriceResponse;
import com.mynt.JavaExam.model.dto.ParcelRequest;
import com.mynt.JavaExam.repository.PricingRateRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class DeliveryCalculatorService {

    private static final double MAX_WEIGHT = 50.0;
    private static final double HEAVY_PARCEL_THRESHOLD = 10;
    private static final double SMALL_PARCEL_VOLUME_THRESHOLD = 1500;
    private static final double MEDIUM_PARCEL_VOLUME_THRESHOLD = 2500;
    private static final String CURRENCY = "PHP";

    private final PricingRateRepository pricingRateRepository;
    private final CouponClient couponClient;

    public DeliveryPriceResponse calculateDelivery(ParcelRequest request) {
        ParcelRequest.Parcel parcel = request.getParcel();
        double weight = parcel.getWeight();
        double volume = getVolume(parcel);

        if (weight > MAX_WEIGHT) {
            throw new InvalidInputException("Parcel rejected. Weight exceeds " + MAX_WEIGHT + "kg");
        }

        double deliveryPrice = calculatePrice(weight, volume);
        BigDecimal finalDeliveryPrice = new BigDecimal(deliveryPrice);
        finalDeliveryPrice = getDiscountedDeliveryPrice(request, finalDeliveryPrice);
        return DeliveryPriceResponse.builder()
                .currency(CURRENCY)
                .deliveryPrice(finalDeliveryPrice)
                .build();
    }

    private BigDecimal getDiscountedDeliveryPrice(ParcelRequest request, BigDecimal finalDeliveryPrice) {
        if (request.getVoucherCode() != null) {
            CouponResponseDto couponDiscount = this.couponClient.getCouponDiscount(request.getVoucherCode());
            double discount = couponDiscount.getDiscount();
            finalDeliveryPrice = finalDeliveryPrice.subtract(BigDecimal.valueOf(discount));
        }
        finalDeliveryPrice = finalDeliveryPrice.setScale(2, BigDecimal.ROUND_HALF_UP);
        return finalDeliveryPrice;
    }

    private double calculatePrice(double weight, double volume) {
        List<PriceRate> priceRates = pricingRateRepository.findAll();

        if (weight > HEAVY_PARCEL_THRESHOLD) {
            return findRate(priceRates, "HEAVY_PARCEL")
                    .map(rate -> weight * rate)
                    .orElseThrow(() -> new InvalidInputException("Rate not found for HEAVY_PARCEL"));
        }

        if (volume < SMALL_PARCEL_VOLUME_THRESHOLD) {
            return findRate(priceRates, "SMALL_PARCEL")
                    .map(rate -> volume * rate)
                    .orElseThrow(() -> new InvalidInputException("Rate not found for SMALL_PARCEL"));
        }

        if (volume < MEDIUM_PARCEL_VOLUME_THRESHOLD) {
            return findRate(priceRates, "MEDIUM_PARCEL")
                    .map(rate -> volume * rate)
                    .orElseThrow(() -> new InvalidInputException("Rate not found for MEDIUM_PARCEL"));
        }

        return findRate(priceRates, "LARGE_PARCEL")
                .map(rate -> volume * rate)
                .orElseThrow(() -> new InvalidInputException("Rate not found for LARGE_PARCEL"));
    }

    private Optional<Double> findRate(List<PriceRate> priceRates, String ruleName) {
        return priceRates.stream()
                .filter(pr -> ruleName.equalsIgnoreCase(pr.getRuleName()))
                .map(PriceRate::getRate)
                .findFirst();
    }

    private double getVolume(ParcelRequest.Parcel parcel) {
        return parcel.getHeight() * parcel.getWidth() * parcel.getLength();
    }
}
