package com.company;/*
db.mysql.url="jdbc:mysql://final.crj8desqwwh6.us-east-1.rds.amazonaws.com:3306/db?characterEncoding=UTF-8&useSSL=false"
*/

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author kath
 */
public class Connect {


    /**
     * The name of the MySQL account to use (or empty for anonymous)
     */
    private final String userName = "admin";

    /**
     * The password for the MySQL account (or empty for anonymous)
     */
    private final String password = "12345678";

    /**
     * The name of the computer running MySQL
     */
    private final String serverName = "final.crj8desqwwh6.us-east-1.rds.amazonaws.com";

    /**
     * The port of the MySQL server (default is 3306)
     */
    private final int portNumber = 3306;

    /**
     * The name of the database we are testing with (this default is installed with MySQL)
     */
    private final String dbName = "final";


    /**
     * Get a new database connection
     *
     * @return
     * @throws SQLException
     */
    public Connection getConnection() throws SQLException, IOException {
        Connection conn = null;
        Properties connectionProps = new Properties();
        connectionProps.put("user", this.userName);
        connectionProps.put("password", this.password);

        conn = DriverManager.getConnection("jdbc:mysql://"
                + this.serverName + ":" + this.portNumber + "/" + this.dbName
                + "?characterEncoding=UTF-8&useSSL=false&allowPublicKeyRetrieval=true",
            connectionProps);

        return conn;
    }

    public void connectToDB() {
        Connection conn = null;
        try {
            conn = this.getConnection();
            System.out.println("Connected to database");
        } catch (SQLException | IOException e) {
            System.out.println("ERROR: Could not connect to the database");
        }
    }

    public void closeConnection(Connection conn) throws SQLException {
        conn.close();
        System.out.println("Disconnect success!");
    }

}