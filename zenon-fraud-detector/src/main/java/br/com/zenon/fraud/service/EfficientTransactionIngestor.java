package br.com.zenon.fraud.service;

import br.com.zenon.fraud.domain.Customer;
import br.com.zenon.fraud.domain.Transaction;
import br.com.zenon.fraud.domain.enumerator.TransactionType;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class EfficientTransactionIngestor {

    public static final int FRAUD_LIMIT = 10_000;
    public static final int BATCH_SIZE = 2500;

    private final Semaphore dbPermits = new Semaphore(10);

    public void readAsStream(String filePath, Consumer<Transaction> consumer) {
        try (Stream<String> stream = Files.lines(Paths.get(filePath))) {
            stream.skip(1)
                  .limit(FRAUD_LIMIT)
                  .map(this::getTransaction)
                  .filter(Optional::isPresent)
                  .map(Optional::get)
                  .forEach(transaction -> {
                      consumer.accept(transaction);
                  });
        } catch (IOException e) {
            throw new RuntimeException("Error reading file", e);
        }
    }

    public void readBatch(String filePath, Consumer<List<Transaction>> consumer) {
        List<String> lines = new ArrayList<>(BATCH_SIZE);
        try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
             Stream<String> stream = Files.lines(Paths.get(filePath)).skip(1)) {//.limit(FRAUD_LIMIT)) {

            Iterator<String> iterator = stream.iterator();

            while (iterator.hasNext()) {
                String line = iterator.next();
                lines.add(line);
                if (lines.size() >= BATCH_SIZE) {
                    List<String> copyOfLines = List.copyOf(lines);
                    executorService.submit(() -> executeBatch(consumer, copyOfLines));
                    lines.clear();
                }

            }
            if (!lines.isEmpty()) {
                List<String> copyOfLines = List.copyOf(lines);
                executorService.submit(() -> executeBatch(consumer, List.copyOf(copyOfLines)));
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading file", e);
        }
    }

    private void executeBatch(Consumer<List<Transaction>> consumer, List<String> lines) {
        List<Transaction> transactions = lines
                .stream()
                .map(this::getTransaction)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

        try {
            dbPermits.acquire();
            try {
                consumer.accept(transactions);
            } finally {
                dbPermits.release();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

    }

    private Optional<Transaction> getTransaction(String line) {
        String[] fields = line.split(",");
        try {
            int step = Integer.parseInt(fields[0]);
            TransactionType transactionType = TransactionType.valueOf(fields[1]);

            if (fields[2] == null || fields[2].isEmpty()) {
                throw new IllegalArgumentException("Amount must not be null or empty");
            }
            BigDecimal amount = new BigDecimal(fields[2]);

            if (fields[4] == null || fields[4].isBlank()) {
                throw new IllegalArgumentException("OldBalanceOrig must not be null or empty");
            }
            if (fields[5] == null || fields[5].isBlank()) {
                throw new IllegalArgumentException("NewBalanceOrig must not be null or empty");
            }
            Customer customerOrigin = new Customer(fields[3], new BigDecimal(fields[4]), new BigDecimal(fields[5]));

            if (fields[7] == null || fields[7].isBlank()) {
                throw new IllegalArgumentException("OldBalanceDest must not be null or empty");
            }
            if (fields[8] == null || fields[8].isBlank()) {
                throw new IllegalArgumentException("NewBalanceDest must not be null or empty");
            }
            Customer customerRecipient = new Customer(fields[6], new BigDecimal(fields[7]), new BigDecimal(fields[8]));
            boolean isFraud = "1".equals(fields[9]);
            boolean isFlaggedFraud = "1".equals(fields[10]);
            Transaction transaction = new Transaction(step, transactionType, amount, customerOrigin, customerRecipient, isFraud, isFlaggedFraud);
            return Optional.of(transaction);
        } catch (Exception e) {
            System.err.println("Erro: " + line);
            return Optional.empty();
        }
    }
}
