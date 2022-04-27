package models;


import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class User {

    private int userId;
    private String userName;
    private String password;

    public User() {
    }

    public User(int userId, String userName, String password) {
        this.userId = userId;
        this.userName = userName;
        this.password = password;
    }

    public static boolean userInsert(Connection conn, String name, String password)
        throws SQLException {
        try (CallableStatement statement = conn.prepareCall("{call insert_user(?,?)}");) {

            statement.setString(1, name);
            statement.setString(2, password);

            statement.execute();
            statement.close();
            System.out.println("Insert user procedure called successfully!");
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
