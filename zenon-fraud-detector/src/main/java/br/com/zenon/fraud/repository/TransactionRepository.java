package br.com.zenon.fraud.repository;

import br.com.zenon.fraud.domain.Transaction;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository {

    Optional<Transaction> getTransactionByCustomerOriginName(String customerOriginName);

    void save(Transaction transaction);

    void saveAll(List<Transaction> transactions);
}
