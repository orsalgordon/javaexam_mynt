package com.mynt.JavaExam.repository;

import com.mynt.JavaExam.model.PriceRate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PricingRateRepository extends JpaRepository<PriceRate, Long> {
}
