package br.com.zenon.fraud.domain;

import java.math.BigDecimal;

public record Customer(
        String name,
        BigDecimal oldBalance,
        BigDecimal newBalance) {}
