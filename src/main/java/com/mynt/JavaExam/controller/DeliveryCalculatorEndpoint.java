package com.mynt.JavaExam.controller;

import com.mynt.JavaExam.model.dto.DeliveryPriceResponse;
import com.mynt.JavaExam.model.dto.ParcelRequest;
import com.mynt.JavaExam.service.DeliveryCalculatorService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/rest/v1/delivery")
@Tag(name = "Delivery Calculator")
public class DeliveryCalculatorEndpoint {

    @Autowired
    private DeliveryCalculatorService deliveryCalculatorService;

    @PostMapping("/calculate")
    public ResponseEntity<DeliveryPriceResponse> calculateDeliveryPrice(@Valid  @RequestBody ParcelRequest request) {
        return ResponseEntity.ok(deliveryCalculatorService.calculateDelivery(request));
    }
}
