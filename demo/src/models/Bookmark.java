package models;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;


public class Bookmark {


    private int bookmarkId;
    private int userId;
    private int restaurantId;
    private String bookmarkDesp;

    public Bookmark() {
    }

    public Bookmark(int bookmarkId, int userId, int restaurantId, String bookmarkDesp) {
        this.bookmarkId = bookmarkId;
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.bookmarkDesp = bookmarkDesp;
    }

    public static boolean bookmarkInsert(Connection conn, int userId, int restaurantId, String desp)
        throws SQLException {
        try (CallableStatement statement = conn.prepareCall("{call insert_bookmark(?,?,?)}");) {

            statement.setInt(1, userId);
            statement.setInt(2, restaurantId);
            statement.setString(3, desp);

            statement.execute();
            statement.close();

            System.out.println("Insert bookmark procedure called successfully!");
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean delete(Connection conn, int bookmarkId) throws SQLException {
        try (CallableStatement statement = conn.prepareCall("{call delete_bookmark(?)}");) {
            statement.setInt(1, bookmarkId);
            statement.execute();
            statement.close();
            System.out.println("Delete bookmark procedure called successfully!");
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteExact(Connection conn, int restaurantId, int userId) throws SQLException {
        try (CallableStatement statement = conn.prepareCall("{call delete_exact_bookmark(?,?)}");) {
            statement.setInt(1, userId);
            statement.setInt(2, restaurantId);
            statement.execute();
            statement.close();
            System.out.println("Delete bookmark procedure called successfully!");
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getBookmarkId() {
        return bookmarkId;
    }

    public void setBookmarkId(int bookmarkId) {
        this.bookmarkId = bookmarkId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getBookmarkDesp() {
        return bookmarkDesp;
    }

    public void setBookmarkDesp(String bookmarkDesp) {
        this.bookmarkDesp = bookmarkDesp;
    }
}
