package br.com.zenon.fraud;

import br.com.zenon.fraud.service.TransactionReport;

import java.util.Optional;

public class ReportMain {

    void main() {
        TransactionReport transactionReport = new TransactionReport();

        String filePath = "data/PS_20174392719_1491204439457_log.csv";
        TransactionReport.Statistics statistics = transactionReport.generateReport(filePath);
        IO.println(String.format("Total de linhas: %d", statistics.totalTransactions()));
        IO.println(String.format("Total de fraudes: %d", statistics.totalFrauds()));
        IO.println(String.format("Valor total transacionado: %.2f", statistics.totalAmount()));
    }
}
