package com.mynt.JavaExam.client;

import com.mynt.JavaExam.model.dto.CouponResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Slf4j
@Service
public class CouponClient {
    private RestClient restClient;

    @Value("${coupon.service.api.key}")
    private String apiKey;

    public CouponClient(@Value("${coupon.service.url}") String url) {
        ClientHttpRequestFactorySettings requestFactorySettings = new ClientHttpRequestFactorySettings(
                Duration.ofSeconds(10) , Duration.ofSeconds(10) , SslBundle.of(null));
        ClientHttpRequestFactory requestFactory = ClientHttpRequestFactories.get(requestFactorySettings);
        this.restClient = RestClient.builder().baseUrl(url).requestFactory(requestFactory).build();
    }

    public CouponResponseDto getCouponDiscount(String voucherCode){
        CouponResponseDto discount = new CouponResponseDto();
        try {
            discount = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/voucher/" + voucherCode)
                            .queryParam("key", apiKey)
                            .build())
                    .retrieve()
                    .body(CouponResponseDto.class);
        } catch (Exception e) {
            log.error("There's an error retrieving coupon discount: ", e);
        }
        return discount;
    }
}
