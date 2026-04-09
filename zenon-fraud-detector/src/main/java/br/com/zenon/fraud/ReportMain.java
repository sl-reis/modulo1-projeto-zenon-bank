package br.com.zenon.fraud;

import br.com.zenon.fraud.service.TransactionReport;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;

public class ReportMain {

    void main() {

        Scanner ler = new Scanner(System.in);
        System.out.printf("Informe o locale: [pt ou en]:\n");
        String localeStr = ler.next();

        String pais = "us";
        if ("pt".equals(localeStr)) {
            pais = "br";
        }

        Locale locale = Locale.of(localeStr, pais);
        ResourceBundle messagesRB = ResourceBundle.getBundle("report", locale);
        NumberFormat integerFormatter = NumberFormat.getIntegerInstance(locale);
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);
        currencyFormatter.setCurrency(Currency.getInstance("USD"));

        TransactionReport transactionReport = new TransactionReport();

        String filePath = "data/PS_20174392719_1491204439457_log.csv";
        TransactionReport.Statistics statistics = transactionReport.generateReport(filePath);

        String textTotalTransactions = messagesRB.getString("MAIN_REPORT_TOTAL_TRANSACTIONS");
        String textTotalFrauds = messagesRB.getString("MAIN_REPORT_TOTAL_FRAUDS");
        String textTotalValues = messagesRB.getString("MAIN_REPORT_TOTAL_AMOUNT");

        IO.println(String.format("%s: %s", textTotalTransactions, integerFormatter.format(statistics.totalTransactions())));
        IO.println(String.format("%s: %s", textTotalFrauds, integerFormatter.format(statistics.totalFrauds())));
        IO.println(String.format("%s: %s", textTotalValues, currencyFormatter.format(statistics.totalAmount())));
    }
}
