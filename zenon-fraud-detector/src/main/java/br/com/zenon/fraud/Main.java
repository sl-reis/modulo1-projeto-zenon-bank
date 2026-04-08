package br.com.zenon.fraud;

import br.com.zenon.fraud.domain.Transaction;
import br.com.zenon.fraud.domain.enumerator.TransactionType;
import br.com.zenon.fraud.repository.TransactionListRepository;
import br.com.zenon.fraud.repository.TransactionMapRepository;
import br.com.zenon.fraud.repository.TransactionRepository;
import br.com.zenon.fraud.service.FraudAnalyzer;
import br.com.zenon.fraud.service.TransactionIngestor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class Main {

    void main() {
        /*Transaction transaction1 = new Transaction(1,
                TransactionType.PAYMENT,
                new BigDecimal("9839.64"),
                new Customer("C1231006815", new BigDecimal("170136.00"), new BigDecimal("160296.36")),
                new Customer("M1979787155", new BigDecimal("0.00"), new BigDecimal("0.00")),
                false,
                false);

        Transaction transaction2 = new Transaction(743,
                TransactionType.CASH_OUT,
                new BigDecimal("850002.52"),
                new Customer("C1280323807", new BigDecimal("850002.52"), new BigDecimal("0.00")),
                new Customer("C873221189", new BigDecimal("6510099.11"), new BigDecimal("7360101.63")),
                true,
                false);

        IO.println("Transaction1: " + transaction1);
        IO.println("Transaction2: " + transaction2);*/

        System.out.println("----------------------------------------------------------------");

        TransactionIngestor transactionIngestor = new TransactionIngestor();
        //long inicio = System.currentTimeMillis();
        List<Transaction> transactions = transactionIngestor.ingest("data/PS_20174392719_1491204439457_log.csv");
        /*long fim = System.currentTimeMillis();
        transactions.stream().limit(10).forEach(System.out::println);
        System.out.println("Versão 01: " + (fim - inicio) + "ms");

        inicio = System.currentTimeMillis();
        List<Transaction> transactions2 = transactionIngestor.ingestNewMethod("data/PS_20174392719_1491204439457_log.csv");
        fim = System.currentTimeMillis();
        transactions2.stream().limit(10).forEach(System.out::println);
        System.out.println("Versão 02: " + (fim - inicio) + "ms");*/

        System.out.println("----------------------------------------------------------------");

        //List<Transaction> transactions3 = transactionIngestor.ingestNewMethod("data/paysim_with_bad_data.csv");
        //transactions3.forEach(System.out::println);
        //System.out.println(transactions3.size());

        System.out.println("----------------------------------------------------------------");

        FraudAnalyzer fraudAnalyzer = new FraudAnalyzer(transactions);
        List<Transaction> fraudulentTransactions = fraudAnalyzer.getFraudulentTransactions();
        List<BigDecimal> topThreeAmountFraudulentTransactions = fraudAnalyzer.getHighestAmountFraudulentTransactions(3);
        System.out.println(String.format("1. Total de fraudes: %d", fraudulentTransactions.size()));
        System.out.println("2. Top 3 fraudes de maior valor: ");
        topThreeAmountFraudulentTransactions.forEach((value) -> {
            System.out.println(String.format("%.2f", value));
        });

        Set<String> customerNamesFromTopThreeFraudulentTransactions = fraudAnalyzer.getCustomerNameFromHighestFraudulentTransactions(5);
        System.out.println("3. Clientes suspeitos: ");
        customerNamesFromTopThreeFraudulentTransactions.forEach(System.out::println);

        System.out.println(String.format("4. Prejuízo total: %s", fraudAnalyzer.getTotalFraudAmount()));
        System.out.println("5. Fraudes por tipo: ");
        Map<TransactionType, Integer> fraudsByTransactionType = fraudAnalyzer.getNumberOfFraudsByTransactionType();
        fraudsByTransactionType.forEach((transactionType, transactionAmount) -> {
            System.out.println(String.format(" - %s: %d", transactionType, transactionAmount));
        });

        System.out.println("----------------------------------------------------------------");

        TransactionRepository transactionRepository = new TransactionListRepository(transactions);
        String customerOriginName = "C1868032458";
        long inicio = System.nanoTime();
        Optional<Transaction> transactionOptional = transactionRepository.getTransactionByCustomerOriginName(customerOriginName);
        long fim = System.nanoTime();
        if (transactionOptional.isPresent()) {
            System.out.println(transactionOptional.get());
        } else {
            System.out.println(String.format("Transação não encontrada para o cliente %s", customerOriginName));
        }
        System.out.println(String.format("Tempo de pesquisa com list: %s (ms)", (fim - inicio) / 1_000_000.0));

        System.out.println("----------------------------------------------------------------");

        transactionRepository = new TransactionMapRepository(transactions);
        inicio = System.nanoTime();
        Optional<Transaction> transactionOptionalMap = transactionRepository.getTransactionByCustomerOriginName(customerOriginName);
        fim = System.nanoTime();
        transactionOptionalMap.ifPresentOrElse(
                transaction -> System.out.println(transaction),
                () -> System.out.println(String.format("Transação não encontrada para o cliente %s", customerOriginName))
        );
        System.out.println(String.format("Tempo de pesquisa com map: %s (ms)", (fim - inicio) / 1_000_000.0));

    }
}