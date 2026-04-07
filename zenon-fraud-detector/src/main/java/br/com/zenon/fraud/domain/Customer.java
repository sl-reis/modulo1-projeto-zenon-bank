package br.com.zenon.fraud.domain;

import java.math.BigDecimal;

public record Customer(
        String name,
        BigDecimal oldBalance,
        BigDecimal newBalance) {

    public Customer {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Customer name must not be null or blank");
        }

        if (oldBalance == null || oldBalance.signum() < 0) {
            throw new IllegalArgumentException("Old balance must not be null or negative");
        }

        if (newBalance == null || newBalance.signum() < 0) {
            throw new IllegalArgumentException("New balance must not be null or negative");
        }
    }
}
