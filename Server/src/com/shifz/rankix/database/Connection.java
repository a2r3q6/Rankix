package com.shifz.rankix.database;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import javax.servlet.http.HttpServlet;
import javax.sql.DataSource;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by shifar on 15/12/15.
 */
public class Connection {

    public static final boolean debugMode = false;

    //Local credentials
    private static final String LC_HOST = "192.168.0.106";
    private static final String LC_PORT = "3306";
    private static final String LC_USERNAME = "root";
    private static final String LC_PASSWORD = "admin123";


    //Remote credentials
    private static final String RM_USERNAME = System.getenv("OPENSHIFT_MYSQL_DB_USERNAME");
    private static final String RM_PASSWORD = System.getenv("OPENSHIFT_MYSQL_DB_PASSWORD");
    private static final String RM_HOST = System.getenv("OPENSHIFT_MYSQL_DB_HOST");
    private static final String RM_PORT = System.getenv("OPENSHIFT_MYSQL_DB_PORT");

    private static final String DATABASE_NAME = "rankix";

    private static final String SQL_CONNECTION_URL = String.format(
            "jdbc:mysql://%s:%s/%s",
            debugMode ? LC_HOST : RM_HOST,
            debugMode ? LC_PORT : RM_PORT,
            DATABASE_NAME);

    public static java.sql.Connection getConnection() {

        /*  try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }*/

        try {
            return getDataSource().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static DataSource getDataSource() {
        MysqlDataSource mysqlDS = null;
        mysqlDS = new MysqlDataSource();
        mysqlDS.setURL(SQL_CONNECTION_URL);
        mysqlDS.setUser(debugMode ? LC_USERNAME : RM_USERNAME);
        mysqlDS.setPassword(debugMode ? LC_PASSWORD : RM_PASSWORD);
        return mysqlDS;

    }
}
