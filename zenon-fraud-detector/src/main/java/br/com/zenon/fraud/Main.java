package br.com.zenon.fraud;

import br.com.zenon.fraud.domain.Customer;
import br.com.zenon.fraud.domain.Transaction;
import br.com.zenon.fraud.domain.enumerator.TransactionType;

import java.math.BigDecimal;

public class Main {

    void main() {
        Transaction transaction1 = new Transaction(1,
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
        IO.println("Transaction2: " + transaction2);
    }
}