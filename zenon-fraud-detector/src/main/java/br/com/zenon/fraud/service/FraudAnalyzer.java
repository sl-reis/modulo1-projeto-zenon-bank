package br.com.zenon.fraud.service;

import br.com.zenon.fraud.domain.Transaction;
import br.com.zenon.fraud.domain.enumerator.TransactionType;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FraudAnalyzer {

    private final List<Transaction> transactions;

    public FraudAnalyzer(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public List<Transaction> getFraudulentTransactions() {
        return getTransactionStream()
                .toList();
    }

    public List<BigDecimal> getHighestAmountFraudulentTransactions(Integer numberOfFraudulentTransactions) {
        return getTransactionStream()
                .sorted((t1, t2) -> t2.amount().compareTo(t1.amount()))
                .limit(numberOfFraudulentTransactions)
                .map(Transaction::amount)
                .toList();
    }

    public Set<String> getCustomerNameFromHighestFraudulentTransactions(Integer numberOfFraudulentTransactions) {
        return getTransactionStream()
                .sorted(Comparator.comparing(Transaction::amount).reversed())
                .map((transaction -> transaction.customerOrigin().name()))
                .distinct()
                .limit(numberOfFraudulentTransactions)
                .collect(Collectors.toSet());
    }

    public BigDecimal getTotalFraudAmount() {
        return getTransactionStream()
                .map(Transaction::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Map<TransactionType, Integer> getNumberOfFraudsByTransactionType() {
        return getTransactionStream()
                .collect(Collectors.groupingBy(Transaction::transactionType, Collectors.summingInt(t -> 1)));
    }

    private Stream<Transaction> getTransactionStream() {
        return transactions.stream()
                .filter(Transaction::isFraud);
    }
}
