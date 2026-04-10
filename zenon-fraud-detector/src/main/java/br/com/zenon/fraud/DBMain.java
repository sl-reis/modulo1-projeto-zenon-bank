package br.com.zenon.fraud;

import br.com.zenon.fraud.domain.Transaction;
import br.com.zenon.fraud.repository.TransactionSQLRepository;
import br.com.zenon.fraud.service.TransactionIngestor;

import java.util.List;

public class DBMain {

    void main() {
        TransactionIngestor transactionIngestor = new TransactionIngestor();

        List<Transaction> listTransactions = transactionIngestor.ingest("data/PS_20174392719_1491204439457_log.csv");

        TransactionSQLRepository  transactionSQLRepository = new TransactionSQLRepository();
        long inicio = System.nanoTime();
        transactionSQLRepository.saveAll(listTransactions);
        long fim = System.nanoTime();

        System.out.println(String.format("Tempo de inserção: %s (ms)", (fim - inicio) / 1_000_000.0));

        transactionSQLRepository.getTransactionByCustomerOriginName("C1231006815").ifPresentOrElse(
                transaction -> System.out.println("Cliente C1231006815 encontrado: " + transaction),
                        () -> System.out.println("Cliente C1231006815 não encontrado!"));

        transactionSQLRepository.getTransactionByCustomerOriginName("C1234").ifPresentOrElse(
                transaction -> System.out.println("Cliente C1234 encontrado: " + transaction),
                () -> System.out.println("Cliente C1234 não encontrado!"));
    }
}
