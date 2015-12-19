package com.shifz.rankix.database;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by shifar on 15/12/15.
 */
public class Connection {

    public static final boolean debugMode = false;

    public static java.sql.Connection getConnection() {

        try {
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource ds = (DataSource) envContext.lookup(debugMode ? "jdbc/rankix" :"jdbc/MySQLDS");
            return ds.getConnection();
        } catch (NamingException | SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
