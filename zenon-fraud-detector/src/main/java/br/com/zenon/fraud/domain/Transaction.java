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
        boolean isFlaggedFraud) {}
