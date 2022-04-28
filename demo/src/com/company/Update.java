package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Update {

    // Get user input.
    public static String getInput() throws IOException {

        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);
        String input = "";
        try {

            input = br.readLine();
            return input;

        } catch (IOException e) {
            e.printStackTrace();
        }
        br.close();
        return input;
    }

    /**
     * Run an SQL command which does not return a recordset: CREATE/INSERT/UPDATE/DELETE/DROP/etc.
     *
     * @throws SQLException If something goes wrong
     */
    public static boolean executeUpdate(Connection conn, String command) throws SQLException {
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            stmt.executeUpdate(command); // This will throw a SQLException if it fails
            return true;
        } finally {
            // This will run whether we throw an exception or not
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    public static boolean runStatement(Connection conn, String statement) {
        try {
            boolean res = executeUpdate(conn, statement);
            System.out.println(statement);
            return res;
        } catch (SQLException e) {
            System.out.println("ERROR: Could not finish the command");
            e.printStackTrace();
            return false;
        }
    }

}
