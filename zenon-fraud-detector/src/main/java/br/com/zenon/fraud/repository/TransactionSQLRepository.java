package br.com.zenon.fraud.repository;

import br.com.zenon.fraud.domain.Customer;
import br.com.zenon.fraud.domain.Transaction;
import br.com.zenon.fraud.domain.enumerator.TransactionType;

import java.sql.*;
import java.util.List;
import java.util.Optional;

public class TransactionSQLRepository implements TransactionRepository {


    public static final int BATCH_SIZE = 1_000;

    @Override
    public Optional<Transaction> getTransactionByCustomerOriginName(String customerOriginName) {
        String sql = """
            SELECT step,
                   transaction_type,
                   amount,
                   name_origin,
                   old_balance_origin,
                   new_balance_origin,
                   name_recipient,
                   old_balance_recipient,
                   new_balance_recipient,
                   is_fraud,
                   is_flagged_fraud 
            FROM transactions 
            WHERE name_origin = ?
            ORDER BY step
        """;
        try (Connection connection = ConnectionFactory.getInstance().getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, customerOriginName);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(new Transaction(
                        resultSet.getInt("step"),
                        TransactionType.valueOf(resultSet.getString("transaction_type")),
                        resultSet.getBigDecimal("amount"),
                        new Customer(
                                resultSet.getString("name_origin"),
                                resultSet.getBigDecimal("old_balance_origin"),
                                resultSet.getBigDecimal("new_balance_origin")
                        ),
                        new Customer(
                                resultSet.getString("name_recipient"),
                                resultSet.getBigDecimal("old_balance_recipient"),
                                resultSet.getBigDecimal("new_balance_recipient")
                        ),
                        resultSet.getBoolean("is_fraud"),
                        resultSet.getBoolean("is_flagged_fraud")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    @Override
    public void save(Transaction transaction) {
        saveAll(List.of(transaction)); // compatibilidade com quem ja usa save unitario
    }

    @Override
    public void saveAll(List<Transaction> listTransactions) {
        if (listTransactions == null || listTransactions.isEmpty()) {
            return;
        }

        final String insertTransactionSql =
        """
                 insert into transactions 
                     (step, 
                      transaction_type, 
                      amount, 
                      name_origin, 
                      old_balance_origin, 
                      new_balance_origin, 
                      name_recipient, 
                      old_balance_recipient, 
                      new_balance_recipient, 
                      is_fraud, 
                      is_flagged_fraud)
                 values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        int count = 0;

        try (Connection connection = ConnectionFactory.getInstance().getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement insertTransactionStmt = connection.prepareStatement(insertTransactionSql)) {

                for (Transaction transaction : listTransactions) {
                    insertTransactionStmt.setInt(1, transaction.step());
                    insertTransactionStmt.setString(2, transaction.transactionType().name());
                    insertTransactionStmt.setBigDecimal(3, transaction.amount());
                    insertTransactionStmt.setString(4, transaction.customerOrigin().name());
                    insertTransactionStmt.setBigDecimal(5, transaction.customerOrigin().oldBalance());
                    insertTransactionStmt.setBigDecimal(6, transaction.customerOrigin().newBalance());
                    insertTransactionStmt.setString(7, transaction.customerRecipient().name());
                    insertTransactionStmt.setBigDecimal(8, transaction.customerRecipient().oldBalance());
                    insertTransactionStmt.setBigDecimal(9, transaction.customerRecipient().newBalance());
                    insertTransactionStmt.setBoolean(10, transaction.isFraud());
                    insertTransactionStmt.setBoolean(11, transaction.isFlaggedFraud());
                    insertTransactionStmt.addBatch();

                    count++;
                    if (count % BATCH_SIZE == 0) {
                        insertTransactionStmt.executeBatch();
                    }
                }

                if (count % BATCH_SIZE != 0) {
                    insertTransactionStmt.executeBatch();
                }
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving transactions", e);
        }
    }
}
