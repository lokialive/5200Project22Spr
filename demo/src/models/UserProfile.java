package models;


import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UserProfile {


    private int userId;
    private String lastName;
    private String firstName;
    private String phoneNumber;
    private String address;
    private Date dateOfBirth;

    public UserProfile() {
    }

    public UserProfile(int userId, String lastName, String firstName, String phoneNumber,
        String address, Date dateOfBirth) {
        this.userId = userId;
        this.lastName = lastName;
        this.firstName = firstName;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
    }

    public UserProfile(int userId, String lastName, String firstName) {
        this.userId = userId;
        this.lastName = lastName;
        this.firstName = firstName;
    }

    public static boolean profileInsert(Connection conn, int userId) throws SQLException {
        try (CallableStatement statement = conn.prepareCall("{call insert_profile(?,?,?,?,?,?)}");) {
            statement.setInt(1, userId);
            statement.setNull(2, Types.VARCHAR);
            statement.setNull(3, Types.VARCHAR);
            statement.setNull(4, Types.VARCHAR);
            statement.setNull(5, Types.VARCHAR);
            statement.setNull(6, Types.VARCHAR);
            statement.execute();
            statement.close();

            System.out.println("Insert profile procedure called successfully!");
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public static boolean editName(Connection conn, int userId, String lastname, String firstname)
        throws SQLException {
        try (CallableStatement statement = conn.prepareCall("{call edit_profile_name(?,?,?)}");) {

            statement.setInt(1, userId);
            statement.setString(2, lastname);
            statement.setString(3, firstname);
            statement.execute();
            statement.close();
            System.out.println("Edit profile name procedure called successfully!");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public static boolean editPhone(Connection conn, int userId, String phone) throws SQLException {
        try (CallableStatement statement = conn.prepareCall("{call edit_profile_phone(?,?)}");) {

            statement.setInt(1, userId);
            statement.setString(2, phone);
            statement.execute();
            statement.close();
            System.out.println("Edit profile phone procedure called successfully!");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean editAddress(Connection conn, int userId, String address)
        throws SQLException {
        try (CallableStatement statement = conn.prepareCall("{call edit_profile_address(?,?)}");) {

            statement.setInt(1, userId);
            statement.setString(2, address);
            statement.execute();
            statement.close();
            System.out.println("Edit profile address procedure called successfully!");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

// TODO
    public static boolean editBirth(Connection conn, int userId, String birth) throws SQLException {
        try (CallableStatement statement = conn.prepareCall("{call edit_profile_birth(?,?)}");) {

            statement.setInt(1, userId);
//            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//            Date temp = formatter.parse(birth);
            statement.setDate(2, java.sql.Date.valueOf(birth) );
            statement.execute();
            statement.close();
            System.out.println("Edit profile birth procedure called successfully!");
            return true;
//        } catch (SQLException | ParseException e) {
        } catch (SQLException  e) {
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

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}
