package br.com.zenon.fraud.repository;

import br.com.zenon.fraud.domain.Transaction;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class TransactionMapRepository implements TransactionRepository {

    private final Map<String, Transaction> transactionsMap;

    public TransactionMapRepository(List<Transaction> transactions) {
        Objects.requireNonNull(transactions, "Transactions list cannot be null");

        this.transactionsMap = transactions.stream()
                .collect(Collectors.toMap(transaction -> transaction.customerOrigin().name(), transaction -> transaction));
    }

    @Override
    public Optional<Transaction> getTransactionByCustomerOriginName(String customerOriginName) {
        return Optional.ofNullable(transactionsMap.get(customerOriginName));
    }

    @Override
    public void save(Transaction transaction) {
        transactionsMap.putIfAbsent(transaction.customerOrigin().name(), transaction);
    }

    @Override
    public void saveAll(List<Transaction> transactions) {
        transactions.forEach(this::save);
    }
}
