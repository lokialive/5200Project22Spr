package models;


import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
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

    public static boolean userExist(Connection conn, String name, String password_input)
            throws SQLException {
        try (CallableStatement statement = conn.prepareCall("{call select_user_by_name(?)}");) {

            statement.setString(1, name);

            ResultSet res = statement.executeQuery();
            if(res.next()) {
                //System.out.println(res.getString(3));
                if (res.getString(3).equals(password_input)) {
                    System.out.println("Valid user");
                    statement.close();
                    return true;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        System.out.println("Invalid user");

        return false;
    }
    public static boolean userNameExist(Connection conn, String name, String password_input)
            throws SQLException {
        try (CallableStatement statement = conn.prepareCall("{call select_user_by_name(?)}");) {
            statement.setString(1, name);
            ResultSet res = statement.executeQuery();
            if(res.next()) {
                System.out.println("User name exist, please change another one.");
                    return false;
                }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
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
