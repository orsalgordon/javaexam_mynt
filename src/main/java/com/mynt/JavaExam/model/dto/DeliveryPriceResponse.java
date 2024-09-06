package com.mynt.JavaExam.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliveryPriceResponse {
    private BigDecimal deliveryPrice;
    private String currency;
}
