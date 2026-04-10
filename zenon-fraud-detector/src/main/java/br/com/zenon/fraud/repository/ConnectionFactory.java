package br.com.zenon.fraud.repository;

import com.mysql.cj.jdbc.MysqlDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionFactory {

    private static final ConnectionFactory INSTANCE = new ConnectionFactory();
    private final DataSource dataSource;

    private ConnectionFactory() {
        MysqlDataSource ds = new MysqlDataSource();
        ds.setURL("jdbc:mysql://localhost:3306/transacoes");
        ds.setUser("root");
        ds.setPassword("senha123");
        this.dataSource = ds;
    }

    public static ConnectionFactory getInstance() {
        return INSTANCE;
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
