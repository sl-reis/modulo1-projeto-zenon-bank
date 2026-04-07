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

public class TransactionIngestor {

    public List<Transaction> ingest(String fileName) {
        Path path = Paths.get(fileName);
        List<Transaction> transactions = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(path.toFile()))) {
            //lê o cabeçalho do arquivo
            bufferedReader.readLine();
            int counter = 0;
            String line;
            while (counter < 1000 && (line = bufferedReader.readLine()) != null) {
                if (line != null) {
                    Transaction transaction = getTransaction(line);
                    transactions.add(transaction);
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
                    .limit(1000)
                    .map(this::getTransaction)
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("Error reading file: " + fileName, e);
        }
    }

    private Transaction getTransaction(String line) {
        String[] fields = line.split(",");
        int step = Integer.parseInt(fields[0]);
        TransactionType transactionType = TransactionType.valueOf(fields[1]);
        BigDecimal amount = new BigDecimal(fields[2]);
        Customer customerOrigin = new Customer(fields[3], new BigDecimal(fields[4]), new BigDecimal(fields[5]));
        Customer customerRecipient = new Customer(fields[6], new BigDecimal(fields[7]), new BigDecimal(fields[8]));
        boolean isFraud = "1".equals(fields[9]);
        boolean isFlaggedFraud = "1".equals(fields[10]);
        return new Transaction(step, transactionType, amount, customerOrigin, customerRecipient, isFraud, isFlaggedFraud);
    }
}
