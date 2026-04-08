package br.com.zenon.fraud.repository;

import br.com.zenon.fraud.domain.Transaction;

import java.util.Optional;

public interface TransactionRepository {

    public Optional<Transaction> getTransactionByCustomerOriginName(String customerOriginName);
}
