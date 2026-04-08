package br.com.zenon.fraud.service;

import br.com.zenon.fraud.domain.Customer;
import br.com.zenon.fraud.domain.Transaction;
import br.com.zenon.fraud.domain.enumerator.TransactionType;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

public class TransactionIngestor {

    public static final int FRAUD_LIMIT = 50000;

    public List<Transaction> ingest(String fileName) {
        Path path = Paths.get(fileName);
        List<Transaction> transactions = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(path.toFile()))) {
            //lê o cabeçalho do arquivo
            bufferedReader.readLine();
            int counter = 0;
            String line;
            while (counter < FRAUD_LIMIT && (line = bufferedReader.readLine()) != null) {
                if (line != null) {
                    try {
                        if (getTransaction(line).isEmpty()) {
                            continue;
                        } else {
                            transactions.add(getTransaction(line).get());
                        }
                    } catch (Exception e) {
                        System.err.println("Erro: " + line);
                    }
                }
                counter++;
            }
        } catch (Exception e) {
            throw new RuntimeException("Error reading file: " + fileName, e);
        }
        return transactions;
    }

    public List<Transaction> ingestNewMethod(String fileName) {
        Path path = Paths.get(fileName);
        List<Transaction> transactions = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(path);
            return lines.stream()
                    .skip(1)
                    .limit(FRAUD_LIMIT)
                    .map(this::getTransaction)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("Error reading file: " + fileName, e);
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
