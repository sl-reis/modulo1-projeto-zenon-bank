package br.com.zenon.fraud.service;

import br.com.zenon.fraud.domain.Customer;
import br.com.zenon.fraud.domain.Transaction;
import br.com.zenon.fraud.domain.enumerator.TransactionType;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;

public class TransactionReport {

    private record ReportTransaction(BigDecimal amount, boolean isFraud) {}

    public record Statistics(long totalTransactions, long totalFrauds, BigDecimal totalAmount) {
        private static final Statistics ZERO = new Statistics(0, 0, BigDecimal.ZERO);

        private static Statistics addReportTransaction(Statistics statistics, ReportTransaction rt) {
            return new Statistics(
                    statistics.totalTransactions() + 1,
                    statistics.totalFrauds() + (rt.isFraud() ? 1 : 0),
                    statistics.totalAmount().add(rt.amount())
            );
        }

        private Statistics add(Statistics other) {
            return new Statistics(totalTransactions + other.totalTransactions(),
                    totalFrauds + other.totalFrauds(),
                    totalAmount.add(other.totalAmount()));
        }

    }

    public Statistics generateReport(String filePath) {
        try (Stream<String> stream = Files.lines(Paths.get(filePath))) {
            return stream.skip(1)
                    .map(this::getReportTransaction)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .reduce(
                            Statistics.ZERO,
                            Statistics::addReportTransaction,
                            Statistics::add);
        } catch (IOException e) {
            throw new RuntimeException("Error reading file", e);
        }
    }

    private Optional<ReportTransaction> getReportTransaction(String line) {
        String[] fields = line.split(",");
        try {
            if (fields[2] == null || fields[2].isEmpty()) {
                throw new IllegalArgumentException("Amount must not be null or empty");
            }
            BigDecimal amount = new BigDecimal(fields[2]);

            boolean isFraud = "1".equals(fields[9]);
            ReportTransaction ReportTransaction = new ReportTransaction(amount, isFraud);
            return Optional.of(ReportTransaction);
        } catch (Exception e) {
            System.err.println("Erro: " + line);
            return Optional.empty();
        }
    }
}
