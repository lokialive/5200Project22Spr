package models;


import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class Order {


    private int orderId;
    private int restaurantId;
    private int userId;
    private String orderDesp;

    public Order() {
    }

    public Order(int orderId, int restaurantId, int userId, String orderDesp) {
        this.orderId = orderId;
        this.restaurantId = restaurantId;
        this.userId = userId;
        this.orderDesp = orderDesp;
    }

    public static boolean orderInsert(Connection conn, String orderDesp, int restaurantId,
        int userId)
        throws SQLException {
        try (CallableStatement statement = conn.prepareCall("{call insert_order(?, ?,?)}");) {

            statement.setString(1, orderDesp);
            statement.setInt(2, restaurantId);
            statement.setInt(3, userId);

            statement.execute();
            statement.close();

            System.out.println("Insert order procedure called successfully!");
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public static boolean delete(Connection conn, int orderId) throws SQLException {
        try (CallableStatement statement = conn.prepareCall("{call delete_order(?)}");) {
            statement.setInt(1, orderId);
            statement.execute();
            statement.close();
            System.out.println("Delete order procedure called successfully!");
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getOrderDesp() {
        return orderDesp;
    }

    public void setOrderDesp(String orderDesp) {
        this.orderDesp = orderDesp;
    }
}
