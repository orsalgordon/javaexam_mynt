package com.mynt.JavaExam.model.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ParcelRequest {

    @NotNull
    private Parcel parcel;
    private String voucherCode;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Parcel {
        @NotNull(message = "Weight is required")
        @DecimalMin(value = "0.0", inclusive = true, message = "Weight must be a positive number")
        private Double weight;

        @NotNull(message = "Height is required")
        @DecimalMin(value = "0.0", inclusive = true, message = "Height must be a positive number")
        private Double height;

        @NotNull(message = "Width is required")
        @DecimalMin(value = "0.0", inclusive = true, message = "Width must be a positive number")
        private Double width;

        @NotNull(message = "Length is required")
        @DecimalMin(value = "0.0", inclusive = true, message = "Length must be a positive number")
        private Double length;
    }
}
