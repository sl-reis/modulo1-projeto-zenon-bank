package br.com.zenon.fraud;

import br.com.zenon.fraud.domain.Transaction;
import br.com.zenon.fraud.repository.TransactionSQLRepository;
import br.com.zenon.fraud.service.EfficientTransactionIngestor;

import java.util.List;
import java.util.function.Consumer;

public class IngestionMain {

    void main() {

        TransactionSQLRepository  transactionSQLRepository = new TransactionSQLRepository();

        /*Consumer<Transaction> consumer = transaction -> {
            //System.out.println("Processando transação: " + transaction);
            transactionSQLRepository.save(transaction);
        };

        EfficientTransactionIngestor efficientTransactionIngestor = new EfficientTransactionIngestor();
        long inicio = System.nanoTime();
        efficientTransactionIngestor.readAsStream("data/PS_20174392719_1491204439457_log.csv", consumer);
        long fim = System.nanoTime();
        IO.println(String.format("Tempo de inserção: %s (ms)", (fim - inicio) / 1_000_000.0));*/

        Consumer<List<Transaction>> consumer = transactions -> {
            transactionSQLRepository.saveAll(transactions);
        };

        EfficientTransactionIngestor efficientTransactionIngestor = new EfficientTransactionIngestor();
        long inicio = System.nanoTime();
        efficientTransactionIngestor.readBatch("data/PS_20174392719_1491204439457_log.csv", consumer);
        long fim = System.nanoTime();
        IO.println(String.format("Tempo de inserção: %s (ms)", (fim - inicio) / 1_000_000.0));


    }
}
