package br.com.zenon.fraud.repository;

import br.com.zenon.fraud.domain.Transaction;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class TransactionListRepository implements TransactionRepository {

    private final List<Transaction> transactions;

    public TransactionListRepository(List<Transaction> transactions) {
        Objects.requireNonNull(transactions, "Transactions list cannot be null");
        this.transactions = transactions;
    }

    public Optional<Transaction> getTransactionByCustomerOriginName(String customerOriginName) {
        Optional<Transaction> transactionOptional = transactions.stream()
                .filter(transaction -> transaction.customerOrigin().name().equals(customerOriginName))
                .findFirst();
        return transactionOptional;
    }
}
