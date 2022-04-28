package models;


import com.company.Update;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class Review {

    private int reviewId;
    private int userId;
    private int restaurantId;
    private String content;

    public Review() {
    }

    public Review(int reviewId, int userId, int restaurantId, String content) {
        this.reviewId = reviewId;
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.content = content;
    }

    public Review(int userId, int restaurantId, String content) {
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.content = content;
    }


    public static boolean reviewInsert(Connection conn, String desp, int restaurantId, int userId)
        throws SQLException {
        try (CallableStatement statement = conn.prepareCall("{call insert_review(?,?,?)}");) {
            statement.setInt(1, userId);
            statement.setString(2, desp);
            statement.setInt(3, restaurantId);
            statement.execute();
            statement.close();

            System.out.println("Insert review procedure called successfully!");
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean delete(Connection conn, int reviewId) throws SQLException {
        try (CallableStatement statement = conn.prepareCall("{call delete_review(?)}");) {
            statement.setInt(1, reviewId);
            statement.execute();
            statement.close();
            System.out.println("Delete review procedure called successfully!");
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean edit(Connection conn, int reviewId, String newContent)
        throws SQLException {
        try (CallableStatement statement = conn.prepareCall("{call edit_review(?,?)}");) {
            statement.setInt(1, reviewId);
            statement.setString(2, newContent);
            statement.execute();
            statement.close();
            System.out.println("Edit review procedure called successfully!");
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }




    public int getReviewId() {
        return reviewId;
    }

    public int getUserId() {
        return userId;
    }

    public int getRestaurantId() {
        return restaurantId;
    }

    public String getContent() {
        return content;
    }

    public void setReviewId(int reviewId) {
        this.reviewId = reviewId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }

    public void setContent(String content) {
        this.content = content;
    }
}