package br.com.soulprime.factory;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.stereotype.Component;

/**
 * Classe de conexao com o banco de dados (padrao Connection Factory).
 * Os dados de conexao (url, usuario e senha) ficam no application.properties,
 * lidos das variaveis de ambiente DB_URL, DB_USER e DB_PASSWORD.
 */
@Component
public class ConnectionFactory {

    private final DataSource dataSource;

    public ConnectionFactory(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    //Retorna uma conexao com o banco de dados Oracle
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
