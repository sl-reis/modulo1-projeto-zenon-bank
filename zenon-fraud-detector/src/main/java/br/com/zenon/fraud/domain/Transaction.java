package br.com.zenon.fraud.domain;

import br.com.zenon.fraud.domain.enumerator.TransactionType;

import java.math.BigDecimal;

public record Transaction (
        int step,
        TransactionType transactionType,
        BigDecimal amount,
        Customer customerOrigin,
        Customer customerRecipient,
        boolean isFraud,
        boolean isFlaggedFraud) {

    public Transaction {
        if (step <= 0) {
            throw new IllegalArgumentException("Step must not be zero or negative");
        }

        if (transactionType == null) {
            throw new IllegalArgumentException("Transaction type must not be null");
        }

        if ( amount == null || amount.signum() < 0) {
            throw new IllegalArgumentException("Amount must not be null or negative");
        }

        if (customerOrigin == null) {
            throw new IllegalArgumentException("Customer origin must not be null");
        }

        if (customerRecipient == null) {
            throw new IllegalArgumentException("Customer recipient must not be null");
        }
    }
}
