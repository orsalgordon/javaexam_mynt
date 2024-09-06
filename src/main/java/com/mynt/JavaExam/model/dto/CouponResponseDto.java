package com.mynt.JavaExam.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponResponseDto {
    private String code;
    private double discount;
    private LocalDate expiry;
}
