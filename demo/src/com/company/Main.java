package com.company;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import models.Bookmark;
import models.Order;
import models.Review;
import models.User;
import models.UserProfile;

/**
 * Make sure the JDBC driver is in your classpath. I also offered it in my zip. Named:
 * mysql-connector-java-8.0.22.jar https://dev.mysql.com/downloads/connector/j/
 * <p>
 * take care of the variables usernamestring and passwordstring to use appropriate database
 * credentials before you compile !
 **/

public class Main {

    public static void main(String[] args) throws SQLException, IOException {

        // Connect the database.
        Connect connect = new Connect();
        connect.connectToDB();
        Connection conn = connect.getConnection();

        boolean notDone = true;

        while (notDone) {
            System.out.println("---------- Restaurant Order and Review System ----------");
            System.out.println("Hi, do you have an account?");
            System.out.println(
                "1 - yes, I want to sign in.    2 - No, view as guest   3 - sign up  4 - quit");
            String option = Update.getInput();
            if (option.equals("1")) {
                System.out.println("Please enter username");
                String userName = Update.getInput();
                System.out.println("Please enter password");
                String password = Update.getInput();
                boolean login = false;
                if (User.userExist(conn, userName, password)) {
                    login = true;
                }

                // If user login with correct username and password.
                if (login) {
                    User user = new User();
                    try (CallableStatement statement = conn.prepareCall(
                        "{call select_user_by_name(?)}");) {
                        statement.setString(1, userName);
                        ResultSet res = statement.executeQuery();
                        if (res.next()) {
                            user.setUserId(res.getInt(1));
                            user.setUserName(userName);
                            user.setPassword(password);
                        }

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    loginFunction(conn, user);
                } else {
                    System.out.println("Wrong account");
                }
            } else if (option.equals("2")) {
                // If user continue as a guest
                guestFunction(conn);
            } else if (option.equals("3")) {
                System.out.println("Please enter username");
                String newName = Update.getInput();
                System.out.println("Please enter password");
                String newPassword = Update.getInput();

                if (User.userNameExist(conn, newName, newPassword)) {
                    User.userInsert(conn, newName, newPassword);
                    User user = new User();
                    try (CallableStatement statement = conn.prepareCall(
                        "{call select_user_by_name(?)}");) {
                        statement.setString(1, newName);
                        ResultSet res = statement.executeQuery();
                        if (res.next()) {
                            user.setUserId(res.getInt(1));
                            user.setUserName(newName);
                            user.setPassword(newPassword);
                        }

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    UserProfile.profileInsert(conn, user.getUserId());
                    loginFunction(conn, user);
                }
            } else if (option.equals("4")) {
                break;
            } else {
                System.out.println("Invalid Input!");
            }
            System.out.println("---------------------------------");
            System.out.println("Do you want to continue? (y/n): ");
            String input = Update.getInput();
            if (!input.equalsIgnoreCase("y")) {
                break;
            }
        }
        System.out.println("Thanks for using the restaurant management system!");
    }

    /**
     * If a user login with correct username and password, call this function.
     *
     * @param conn - the database connector
     * @param user - the user which logged in.
     * @throws IOException
     * @throws SQLException
     */
    public static void loginFunction(Connection conn, User user) throws IOException, SQLException {
        while (true) {
            System.out.println("Hi, here are six options that you can choose!");
            System.out.println("Each option would have some sub-options to select! Please enjoy!");
            System.out.println("1 - Restaurants");
            System.out.println("2 - My Orders");
            System.out.println("3 - My Bookmarks");
            System.out.println("4 - My Reviews");
            System.out.println("5 - My Profiles");
            System.out.println("0 - Quit the application");
            System.out.println("Just type the number of the option you want:");
            String option = Update.getInput();

            if (option.equals("0")) {
                break;
            } else if (option.equals("1")) {
                System.out.println("1 - View all restaurants");
                String optionOne = Update.getInput();
                if (optionOne.equals("1")) {
                    ViewAllRestaurants(conn);
                    System.out.println("1 - choose one restaurant number   2 - return");
                    String optionTwo = Update.getInput();
                    if (optionTwo.equals("1")) {
                        System.out.println("Please input the selected restaurant id:");
                        String resStringId = Update.getInput();
                        if(checkId(resStringId)) {
                            selectRestaurantById(conn, resStringId, true, user);
                        }else{
                            System.out.println("Invalid Input! Not an id!");
                        }
                    } else if (optionTwo.equals("2")) {
                        continue;
                    } else {
                        System.out.println("Invalid Input!");
                    }
                } else {
                    System.out.println("Invalid Input!");
                }
            } else if (option.equals("2")) {
                System.out.println("The orders you have");
                outputUserOrders(conn, user);
                System.out.println("1-delete an order   2- return");
                String optionOrder = Update.getInput();
                if (optionOrder.equals("1")) {
                    deleteOrder(conn, user.getUserId());
                } else if (optionOrder.equals("2")) {
                    continue;
                } else {
                    System.out.println("Invalid input!");
                }

            } else if (option.equals("3")) {
                System.out.println("The restaurants you have bookmarked:");
                outputUserBookmarks(conn, user);
                System.out.println("\n" +
                    "1-delete a bookmark   2- return");
                String optionBookmark = Update.getInput();
                if (optionBookmark.equals("1")) {
                    deleteBookmark(conn, user.getUserId());
                } else if (optionBookmark.equals("2")) {
                    continue;
                } else {
                    System.out.println("Invalid input!");
                }
            } else if (option.equals("4")) {
                System.out.println("The reviews you have made:");
                outputUserReviews(conn, user);
                System.out.println("\n" + "1-delete a review  2 - edit a review  3 - return");
                String optionReview = Update.getInput();
                if (optionReview.equals("1")) {
                    deleteReview(conn, user.getUserId());
                } else if (optionReview.equals("2")) {
                    editReview(conn, user.getUserId());
                } else if (optionReview.equals("3")) {
                    continue;
                } else {
                    System.out.println("Invalid input!");
                }

            } else if (option.equals("5")) {
                outputUserProfile(conn, user);
                System.out.println(
                    "1 - edit name  2 - edit phone  3 - edit address  4 - edit birth  5 - return");
                String optionReview = Update.getInput();
                if (optionReview.equals("1")) {
                    System.out.println("Please input last name:");
                    String lastname = Update.getInput();
                    System.out.println("Please input first name:");
                    String firstname = Update.getInput();
                    UserProfile.editName(conn, user.getUserId(), lastname, firstname);
                    continue;
                } else if (optionReview.equals("2")) {
                    System.out.println("Please input the new phone number:");
                    String phoneNumber = Update.getInput();
                    if (checkPhoneValid(phoneNumber)) {
                        UserProfile.editPhone(conn, user.getUserId(), phoneNumber);
                        continue;
                    } else {
                        System.out.println("phone number inout is not valid");
                    }
                } else if (optionReview.equals("3")) {
                    System.out.println("Please input the new address:");
                    String address = Update.getInput();
                    UserProfile.editAddress(conn, user.getUserId(), address);
                } else if (optionReview.equals("4")) {
                    System.out.println("Please input the new birthday(YYYY-MM-DD):");
                    String birth = Update.getInput();
                    if (checkBirthValid(birth)) {
                        UserProfile.editBirth(conn, user.getUserId(), birth);
                    } else {
                        System.out.println("birthday input is not valid");
                    }

                } else if (optionReview.equals("5")) {
                    continue;
                } else {
                    System.out.println("Invalid input!");
                }

            } else {
                System.out.println("Invalid Input, Please try again!");
            }
        }

    }


    /**
     * Check whether the birthday entered is valid.
     *
     * @param birth - input birthday
     * @return - boolean
     */
    private static boolean checkBirthValid(String birth) {
//        Integer year=Integer.valueOf(birth.substring(0,4));
//        Integer month= Integer.valueOf(birth.substring(4,6));
//        Integer day=Integer.valueOf(birth.substring(6,8));

        boolean validate = birth.matches(
            "([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8])))");
        if(validate){
            Integer year = Integer.valueOf(birth.substring(0, 4));
            Integer month = Integer.valueOf(birth.substring(5, 7));
            Integer day = Integer.valueOf(birth.substring(8, 10));
            Integer currentYear = Calendar.getInstance().get(Calendar.YEAR);
            if (month < 0 || month > 12 || day < 0 || day > 31 || year > currentYear) {
                validate = false;
            }
        }
        return validate;
    }

    /**
     * Check whether the phone number input is valid.
     *
     * @param phoneNumber
     * @return - boolean
     */
    private static boolean checkPhoneValid(String phoneNumber) {

        Pattern pattern = Pattern.compile("\\d{10}");
        Matcher res = pattern.matcher(phoneNumber);
        if( !res.matches() )
        {
            return false;
        }
        return true;
    }


    /**
     * Out put the user profile information of the exact user.
     *
     * @param conn - the database connector
     * @param user - the user who called this function
     * @throws SQLException
     */
    private static void outputUserProfile(Connection conn, User user)

        throws SQLException {
        System.out.println(user.getUserName());
        try (CallableStatement statement = conn.prepareCall("{call select_profile(?)}");) {

            statement.setInt(1, user.getUserId());
            ResultSet res = statement.executeQuery();
            while (res.next()) {
                System.out.print("User Id:" + res.getString(1));
                System.out.print(" User Firstname:" + res.getString(2));
                System.out.print(" User Lastname:" + res.getString(3));
                System.out.print(" User Phone Number:" + res.getString(4));
                System.out.print(" User Address:" + res.getString(5));
                System.out.println(" User Date of Birth:" + res.getString(6));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        return;
    }


    private static void outputUserBookmarks(Connection conn, User user) throws SQLException {
        try (CallableStatement statement = conn.prepareCall("{call select_user_bookmark(?)}");) {
            statement.setInt(1, user.getUserId());
            ResultSet res = statement.executeQuery();
            while (res.next()) {
                System.out.print("Bookmark Id :" + res.getString(1));
                System.out.print("    Restaurant Id :" + res.getString(3));
                System.out.print("    Bookmark Description:" + res.getString(4));
            }
            res.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        return;
    }


    private static void outputUserOrders(Connection conn, User user) throws SQLException {
        try (CallableStatement statement = conn.prepareCall("{call select_user_order(?)}");) {
            statement.setInt(1, user.getUserId());
            ResultSet res = statement.executeQuery();
            while (res.next()) {
                System.out.print("Order Id :" + res.getString(1));
                System.out.print("    Restaurant Id :" + res.getString(3));
                System.out.println("  Order Description:" + res.getString(2));

            }
            res.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        return;
    }


    private static void outputUserReviews(Connection conn, User user) throws SQLException {
        try (CallableStatement statement = conn.prepareCall("{call select_user_review(?)}");) {
            statement.setInt(1, user.getUserId());
            ResultSet res = statement.executeQuery();
            while (res.next()) {
                System.out.print("Review Id :" + res.getString(1));
                System.out.print("    Review Content:" + res.getString(3));
                System.out.print("    Restaurant Id :" + res.getString(4));
            }
            res.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        return;
    }

    /**
     * User as a guest.
     *
     * @param conn
     * @throws IOException
     * @throws SQLException
     */
    private static void guestFunction(Connection conn) throws IOException, SQLException {
        while (true) {
            System.out.println("Hi, here are two options that you can choose!");
            System.out.println("Each option would have some sub-options to select! Please enjoy!");
            System.out.println("1 - Restaurants");
            System.out.println("0 - Quit the application");
            System.out.println("Just type the number of the option you want:");
            String option = Update.getInput();

            if (option.equals("0")) {
                break;
            } else if (option.equals("1")) {
                System.out.println("1 - View all restaurants");
                String optionOne = Update.getInput();
                if (optionOne.equals("1")) {
                    ViewAllRestaurants(conn);
                    System.out.println("1 - choose one restaurant number   2 - return");
                    String optionTwo = Update.getInput();
                    if (optionTwo.equals("1")) {
                        System.out.println("Please input the selected restaurant id:");
                        String resStringId = Update.getInput();
                        if(checkId(resStringId)){
                            selectRestaurantById(conn, resStringId, false, null);
                        }else{
                            System.out.println("Invalid Input! Not an id!");
                        }
                    } else if (optionTwo.equals("2")) {
                        continue;
                    } else {
                        System.out.println("Invalid Input!");
                    }
                }
            } else {
                System.out.println("Invalid Input, Please try again!");
            }
        }

    }


    private static void ViewAllRestaurants(Connection conn) throws SQLException {
        try (CallableStatement statement = conn.prepareCall("{call select_all_restaurant()}");) {

            ResultSet res = statement.executeQuery();
            while (res.next()) {

                System.out.print("Restaurant Id:" + res.getString(1));
                System.out.print(" Restaurant Name:" + res.getString(2));
                System.out.print(" Restaurant Phone Number:" + res.getString(3));
                System.out.print(" Restaurant Street Address:" + res.getString(4));
                System.out.print(" Restaurant City:" + res.getString(5));
                System.out.print(" Restaurant State:" + res.getString(6));
                System.out.println(" Restaurant Zip Code:" + res.getString(7));
            }

            res.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        return;
    }


    /**
     * Functions provided after selecting an exact restaurant.
     *
     * @param conn
     * @param resStringId
     * @param login
     * @param user
     * @throws IOException
     * @throws SQLException
     */
    private static void selectRestaurantById(Connection conn, String resStringId, boolean login,
        User user)
        throws IOException, SQLException {
        int restaurantId = Integer.parseInt(resStringId);
        int userId = -1;
        if (login) {
            userId = user.getUserId();
        }

        while (true) {

            System.out.println("By selecting an exact restaurant, you can have several options:");
            System.out.println(
                "1 - view menu   2 - order(only login user)  3 - review   4 - bookmark(only login user)   5 - quit");
            String option = Update.getInput();
            if (option.equals("1")) {
                outputMenuByRestaurantId(conn, restaurantId);
            } else if (option.equals("2")) {
                if (!login) {
                    System.out.println("This function is only available for login user");
                } else {
                    //order login
                    System.out.println("There are your orders in this restaurant:");
                    outputOrderByUserAndRestaurant(conn, userId, restaurantId);
                    System.out.println(
                        "1 - Create new order    2 - delete exist order   3 - return");
                    String optionOrder = Update.getInput();
                    if (optionOrder.equals("1")) {
                        System.out.println("Please input order description:");
                        String desp = Update.getInput();
                        Order.orderInsert(conn, desp, restaurantId, userId);
                    } else if (optionOrder.equals("2")) {
                        deleteOrder(conn, userId);
                    } else if (optionOrder.equals("3")) {
                        continue;
                    } else {
                        System.out.println("Invalid input!");
                    }
                }
            } else if (option.equals("3")) {
                System.out.println("There are the reviews for this restaurant:");
                outputReviewsByRestaurantId(conn, restaurantId);
                // Login user
                if (login) {
                    System.out.println("These reviews are written by you:");
                    outputReviewByUserAndRestaurant(conn, restaurantId, userId);
                    System.out.println("1 - add review  2- delete review  3- edit review ");
                    String optionReview = Update.getInput();
                    if (optionReview.equals("1")) {
                        System.out.println("Please input the review content:");
                        String content = Update.getInput();
                        Review.reviewInsert(conn, content, restaurantId, userId);
                    } else if (optionReview.equals("2")) {
                        deleteReview(conn, userId);
                    } else if (optionReview.equals("3")) {
                        editReview(conn, userId);
                    } else {
                        System.out.println("Invalid Input!");
                    }
                }
            } else if (option.equals("4")) {
                //bookmark
                if (!login) {
                    System.out.println("This function is only available for login user");
                } else {
                    //bookmark login
                    outputBookmarkByUserAndRestaurant(conn, userId, restaurantId);
                }
            } else if (option.equals("5")) {
                break;
            } else {
                System.out.println("Invalid output!");
            }
        }
    }

    private static void outputOrderByUserAndRestaurant(Connection conn, int userId,
        int restaurantId)
        throws IOException, SQLException {
        try (CallableStatement statement = conn.prepareCall("{call select_restaurant_order(?)}");) {
            statement.setInt(1, restaurantId);
            ResultSet res = statement.executeQuery();
            while (res.next()) {
                if (res.getInt(4) == userId) {
                    System.out.print("Order Id:" + res.getString(1));
                    System.out.println(" Order Description:" + res.getString(2));
                }
            }
            res.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        return;
    }

    private static void outputBookmarkByUserAndRestaurant(Connection conn, int userId,
        int restaurantId)
        throws IOException, SQLException {

        boolean hasBookmark = false;
        try (CallableStatement statement = conn.prepareCall("{call select_user_bookmark(?)}");) {
            statement.setInt(1, userId);
            ResultSet res = statement.executeQuery();
            while (res.next()) {
                if (res.getInt(3) == (restaurantId)) {
                    hasBookmark = true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // System.out.println(hasBookmark);

        if (hasBookmark) {
            System.out.println(
                "You have this restaurant bookmarked, do you want to delete it? y/n");
            String option = Update.getInput();
            if (option.equals("y")) {
                Bookmark.deleteExact(conn, restaurantId, userId);
            }
        } else {
            System.out.println(
                "You have not mark this restaurant, do you want to add a bookmark? y/n");
            String option = Update.getInput();
            if (option.equals("y")) {
                System.out.println("Please input bookmark description:");
                String content = Update.getInput();
                Bookmark.bookmarkInsert(conn, userId, restaurantId, content);
            }
        }
    }

    //edit OK
    private static void outputReviewByUserAndRestaurant(Connection conn, int restaurantId,
        int userId) throws SQLException {
        try (CallableStatement statement = conn.prepareCall(
            "{call select_restaurant_review(?)}");) {
            statement.setInt(1, restaurantId);
            ResultSet res = statement.executeQuery();
            while (res.next()) {
                if (res.getInt(2) == userId) {
                    System.out.print("Restaurant Id:" + res.getString(1));
                    System.out.println(" Review Content:" + res.getString(3));
                }
            }
            res.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        return;
    }

    private static void outputMenuByRestaurantId(Connection conn, int restaurantId)
        throws SQLException {
        try (CallableStatement statement = conn.prepareCall("{call select_restaurant_menu(?)}");) {
            statement.setInt(1, restaurantId);
            ResultSet res = statement.executeQuery();
            while (res.next()) {
                System.out.print("Menu Name:" + res.getString(2));
                System.out.println(" Menu Description:" + res.getString(3));
            }
            res.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        return;
    }

    private static void outputReviewsByRestaurantId(Connection conn, int restaurantId)
        throws SQLException {
        try (CallableStatement statement = conn.prepareCall(
            "{call select_restaurant_review(?)}");) {
            statement.setInt(1, restaurantId);
            ResultSet res = statement.executeQuery();
            while (res.next()) {

                System.out.print("Restaurant Id:" + res.getString(1));
                System.out.print(" User Id:" + res.getString(2));
                System.out.println(" Review Content:" + res.getString(3));

            }
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        return;
    }


    public static void editReview(Connection conn, int askId) throws IOException, SQLException {
        System.out.println("Please input the review number you want to edit:");
        String number = Update.getInput();
        if(checkId(number)) {
            if (checkPermitReview(conn, askId, number)) {
                System.out.println("Please input new review:");
                String newReview = Update.getInput();
                Review.edit(conn, Integer.valueOf(number), newReview);
            }
        }else{
            System.out.println("Invalid Input! Not an id!");
        }
    }


    private static boolean checkPermitReview(Connection conn, int askId, String number)
        throws SQLException {
        try (CallableStatement statement = conn.prepareCall("{call select_user_review(?)}");) {
            statement.setInt(1, askId);
            ResultSet res = statement.executeQuery();
            while (res.next()) {
                if (res.getString(1).equals(number)) {
                    return true;
                }
            }
            res.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public static void deleteReview(Connection conn, int askId) throws SQLException, IOException {
        System.out.println("Please input the review number you want to delete:");
        String number = Update.getInput();
        if(checkId(number)) {
            if (checkPermitReview(conn, askId, number)) {
                Review.delete(conn, Integer.parseInt(number));
            } else {
                System.out.println("You do not have this review.");
            }
        }else{
            System.out.println("Invalid Input! Not an id!");
        }
    }

    public static void deleteBookmark(Connection conn, int askId) throws IOException, SQLException {
        System.out.println("Please input the bookmark number you want to delete:");
        String number = Update.getInput();
        if(checkId(number)) {
            if (checkPermitBookmark(conn, askId, number)) {
                Bookmark.delete(conn, Integer.parseInt(number));
            } else {
                System.out.println("You do not have this bookmark.");
            }
        }else{
            System.out.println("Invalid Input! Not an id!");
        }
    }


    //edit ：check number在不在askid的bookmarkid里面
    private static boolean checkPermitBookmark(Connection conn, int askId, String number)
        throws SQLException {
        try (CallableStatement statement = conn.prepareCall("{call select_user_bookmark(?)}");) {
            statement.setInt(1, askId);
            ResultSet res = statement.executeQuery();
            while (res.next()) {
                if (res.getString(1).equals(number)) {
                    return true;
                }
            }
            res.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    private static void deleteOrder(Connection conn, int askId) throws IOException, SQLException {

        System.out.println("Please input the order number you want to delete:");

        String number = Update.getInput();
        if(checkId(number)){
            if (checkPermitOrder(conn, askId, number)) {
                Order.delete(conn, Integer.parseInt(number));
            } else {
                System.out.println("You do not have this order.");
            }
        }else{
            System.out.println("Invalid Input! Not an id!");
        }
    }

    private static boolean checkPermitOrder(Connection conn, int askId, String number)
        throws SQLException {
        try (CallableStatement statement = conn.prepareCall("{call select_user_order(?)}");) {
            statement.setInt(1, askId);
            ResultSet res = statement.executeQuery();
            while (res.next()) {
                if (res.getString(1).equals(number)) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }


    /**
     * Check whether an input string is a valid ID, which includes only numbers.
     * @param inputId
     * @return
     */
    private static boolean checkId(String inputId){
        boolean validate = inputId.matches("^[0-9]*$");
        return validate;
    }

}
