package com.company;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;

import models.Bookmark;
import models.Order;
import models.Review;
import models.User;
import models.UserProfile;

/**
 * Make sure the Postgresql JDBC driver is in your classpath. I also offered it in my zip. Named:
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
                // edit OK
                // 调用recusor得到结果集，判断是否匹配
                if(User.userExist(conn,userName,password)) login=true;

                if (login) {
                    //edit OK
                    //获得这个user包括id （get user by username ）
                    User user = new User();
                    try (CallableStatement statement = conn.prepareCall("{call select_user_by_name(?)}");) {
                        statement.setString(1, userName);
                        ResultSet res = statement.executeQuery();
                        if(res.next()) {
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
                guestFunction(conn);
            } else if (option.equals("3")) {
                System.out.println("Please enter username");
                String newName = Update.getInput();
                System.out.println("Please enter password");
                String newPassword = Update.getInput();

                //edit OK
                //获得这个user包括id （get user by username）
                if(User.userNameExist(conn,newName,newPassword)){
                    User.userInsert(conn, newName, newPassword);
                User user = new User();
                    try (CallableStatement statement = conn.prepareCall("{call select_user_by_name(?)}");) {
                        statement.setString(1, newName);
                        ResultSet res = statement.executeQuery();
                        if(res.next()) {
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
                System.out.println("1 - select restaurant by name   2 - View all restaurants");
                String optionOne = Update.getInput();
                if (optionOne.equals("1")) {
                    System.out.println("Please input restaurant name:");
                    String name = Update.getInput();
                    String resStringId = findRestaurantIdByName(conn,name);
                    if (resStringId.equals("No such name.")) {
                        System.out.println("There is no restaurant matched.");
                    } else {
                        selectRestaurantById(conn,resStringId, true, user);
                    }
                } else if (optionOne.equals("2")) {
                    ViewAllRestaurants(conn);
                    System.out.println("1 - choose one restaurant name   2 - return");
                    String optionTwo = Update.getInput();
                    if (optionTwo.equals("1")) {
                        System.out.println("Please input the selected restaurant id:");
                        String resStringId = Update.getInput();
                        selectRestaurantById(conn,resStringId, true, user);
                    } else if (optionTwo.equals("2")) {
                        break;
                    } else {
                        System.out.println("Invalid Input!");
                    }
                } else {
                    System.out.println("Invalid Input!");
                }
            } else if (option.equals("2")) {
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
                outputUserBookmarks(conn, user);
                System.out.println("1-delete a bookmark   2- return");
                String optionBookmark = Update.getInput();
                if (optionBookmark.equals("1")) {
                    deleteBookmark(conn, user.getUserId());
                } else if (optionBookmark.equals("2")) {
                    continue;
                } else {
                    System.out.println("Invalid input!");
                }
            } else if (option.equals("4")) {
                outputUserReviews(conn, user);
                System.out.println("1-delete a review  2 - edit a review  3 - return");
                String optionReview = Update.getInput();
                if (optionReview.equals("1")) {
                    deleteReview(conn,user.getUserId());
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
                } else if (optionReview.equals("2")) {
                    System.out.println("Please input the new phone number:");
                    String phoneNumber = Update.getInput();
                    if (checkPhoneValid(phoneNumber)) {
                        UserProfile.editPhone(conn, user.getUserId(), phoneNumber);
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

    //edit OK
    private static String findRestaurantIdByName(Connection conn,String name) throws SQLException {
        try (CallableStatement statement = conn.prepareCall("{call select_restaurant_id_by_name(?)}");) {

            statement.setString(1, name);

            ResultSet res = statement.executeQuery();
            if(res.next()) {
                //System.out.println(res.getString(3));

                if (res.getString(2).equals(name)) {
                    String res_id= res.getString(1);
                    statement.close();
                    return res_id;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "error";
        }
        return "No such name.";
    }

    //edit OK
    private static boolean checkBirthValid(String birth) {
//        Integer year=Integer.valueOf(birth.substring(0,4));
//        Integer month= Integer.valueOf(birth.substring(4,6));
//        Integer day=Integer.valueOf(birth.substring(6,8));
                Integer year=Integer.valueOf(birth.substring(0,4));
        Integer month= Integer.valueOf(birth.substring(5,7));
        Integer day=Integer.valueOf(birth.substring(8,10));


        Integer currentYear= Calendar.getInstance().get(Calendar.YEAR);
        if( month<0 || month>12  || day<0 || day>31 || year>currentYear ) return false;
        return true;
    }

    //edit OK
    private static boolean checkPhoneValid(String phoneNumber) {
        if(phoneNumber.length()!=10) return false;
        return true;
    }


    //edit OK
    private static void outputUserProfile(Connection conn, User user)

        throws SQLException {
        System.out.println(user.getUserName());
            try (CallableStatement statement = conn.prepareCall("{call select_profile(?)}");) {

                statement.setInt(1, user.getUserId());
                ResultSet res = statement.executeQuery();
                while(res.next()) {
                    System.out.print("User Id:"+res.getString(1));
                    System.out.print(" User Firstname:"+res.getString(2));
                    System.out.print(" User Lastname:"+res.getString(3));
                    System.out.print(" User Phone Number:"+res.getString(4));
                    System.out.print(" User Address:"+res.getString(5));
                    System.out.println(" User Date of Birth:"+res.getString(6));
                }

            } catch (SQLException e) {
                e.printStackTrace();
                return ;
            }

            return;
        }



    //edit OK
    private static void outputUserBookmarks(Connection conn, User user) throws SQLException {
        try (CallableStatement statement = conn.prepareCall("{call select_user_bookmark(?)}");) {
            statement.setInt(1, user.getUserId());
            ResultSet res = statement.executeQuery();
            while(res.next()) {

                System.out.print("Restaurant Id :"+res.getString(3));
                System.out.print("  Bookmark Description:"+res.getString(4));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return ;
        }
        return;
    }

    //edit OK
    private static void outputUserOrders(Connection conn, User user)throws SQLException {
        try (CallableStatement statement = conn.prepareCall("{call select_user_order(?)}");) {
            statement.setInt(1, user.getUserId());
            ResultSet res = statement.executeQuery();
            while(res.next()) {
                System.out.print("Restaurant Id :"+res.getString(3));
                System.out.println(" Order Description:"+res.getString(2));

            }
        } catch (SQLException e) {
            e.printStackTrace();
            return ;
        }
        return;
    }

    //edit OK
    private static void outputUserReviews(Connection conn, User user) throws SQLException {
        try (CallableStatement statement = conn.prepareCall("{call select_user_review(?)}");) {
            statement.setInt(1, user.getUserId());
            ResultSet res = statement.executeQuery();
            while(res.next()) {
                System.out.print("Review Content:"+res.getString(3));
                System.out.print(" Restaurant Id :"+res.getString(4));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return ;
        }
        return;
    }

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
                System.out.println(
                    "1- Search restaurant by name    2-View all restaurants   3-quit");
                String optionOne = Update.getInput();
                if (optionOne.equals("1")) {
                    System.out.println("Please input restaurant name:");
                    String name = Update.getInput();
                    String resStringId = findRestaurantIdByName(conn,name);
                    if (resStringId.equals("No such name.")) {
                        System.out.println("There is no restaurant matched.");
                    } else {
                        selectRestaurantById(conn,resStringId, false,null);
                    }
                } else if (optionOne.equals("2")) {
                    ViewAllRestaurants(conn);
                    System.out.println("1 - choose one restaurant number   2 - return");
                    String optionTwo = Update.getInput();
                    if (optionTwo.equals("1")) {
                        System.out.println("Please input the restaurant number:");
                        String restaurantId = Update.getInput();
                        selectRestaurantById(conn,restaurantId, false, null);
                    } else if (optionTwo.equals("2")) {
                        break;
                    } else {
                        System.out.println("Invalid Input!");
                    }
                } else if (optionOne.equals("3")) {
                    break;
                } else {
                    System.out.println("Invalid Input!");
                }
            } else {
                System.out.println("Invalid Input, Please try again!");
            }
        }

    }

    //edit OK
    //输出所有的restaurants，写一个procedure（看需不需要recursor），调用
    private static void ViewAllRestaurants(Connection conn) throws SQLException {
        try (CallableStatement statement = conn.prepareCall("{call select_all_restaurant()}");) {

            ResultSet res = statement.executeQuery();
            while(res.next()) {

                System.out.print("Restaurant Id:"+res.getString(1));
                System.out.print(" Restaurant Name:"+res.getString(2));
                System.out.print(" Restaurant Phone Number:"+res.getString(3));
                System.out.print(" Restaurant Street Address:"+res.getString(4));
                System.out.print(" Restaurant City:"+res.getString(5));
                System.out.print(" Restaurant State:"+res.getString(6));
                System.out.println(" Restaurant Zip Code:"+res.getString(7));
            }


        } catch (SQLException e) {
            e.printStackTrace();
            return ;
        }

        return;
    }



    //edit
    //选定一个restaurant后，进行的操作,分为login和非login
    //    选定restaurant后
    //1 view menu: output该餐厅所有的menu
    //2 order功能(login = true)
    //	1 新建order
    //	2 删除order
    //	3 return
    //3 review功能
    //        展示该餐厅所有的review
    //4 bookmark功能(login = true)
    //    显示现在是否有bookmark
    //    若有,可选择unmark;若没有,可选择mark
    private static void selectRestaurantById(Connection conn,String resStringId, boolean login, User user)
        throws IOException, SQLException {
        int restaurantId = Integer.parseInt(resStringId);
        int userId = -1;
        if(login) {
            userId = user.getUserId();
        }

        while (true) {

            System.out.println("By selecting an exact restaurant, you can have several options:");
            System.out.println(
                "1 - view menu   2 - order(only login user)  3 - review   4 - bookmark(only login user)   5 - quit");
            String option = Update.getInput();
            if (option.equals("1")) {
                outputMenuByRestaurantId(conn,restaurantId);
            } else if (option.equals("2")) {
                if (!login) {
                    System.out.println("This function is only available for login user");
                } else {
                    //order login
                    System.out.println("1 - Create new order    2 - delete exist order   3 - return");
                    String optionOrder = Update.getInput();
                    if(optionOrder.equals("1")){
                        System.out.println("Please input order description:");
                        String desp = Update.getInput();
                        Order.orderInsert(conn,desp,restaurantId,userId);
                    }else if(optionOrder.equals("2")){
                        deleteOrder(conn,userId);
                    }else if(optionOrder.equals("3")){
                        continue;
                    }else{
                        System.out.println("Invalid input!");
                    }
                }
            } else if (option.equals("3")) {
                outputReviewsByRestaurantId(conn,restaurantId);
                // Login user
                if (login) {
                    System.out.println("These reviews are written by you:");
                    outputReviewByUserAndRestaurant(conn,restaurantId, userId);
                    System.out.println("1 - add review  2- delete review  3- edit review ");
                    String optionReview = Update.getInput();
                    if (optionReview.equals("1")) {
                        System.out.println("Please input the review content:");
                        String content = Update.getInput();
                        Review.reviewInsert(conn, content, restaurantId, userId);
                    } else if (optionReview.equals("2")) {
                        deleteReview(conn,userId);
                    } else if (optionReview.equals("3")) {
                        editReview(conn,userId);
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
                    outputBookmarkByUserAndRestaurant(conn,userId,restaurantId);
                }
            }else if (option.equals("5")){
                break;
            }
            else {
                System.out.println("Invalid output!");
            }
        }
    }


    private static void outputBookmarkByUserAndRestaurant(Connection conn, int userId, int restaurantId)
        throws IOException, SQLException {

        boolean hasBookmark =false ;
        try (CallableStatement statement = conn.prepareCall("{call select_user_bookmark(?)}");) {
            statement.setInt(1, userId);
            ResultSet res = statement.executeQuery();
            while (res.next()) {
                if (res.getInt(3)==(restaurantId))
                    hasBookmark= true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println(hasBookmark);

        if(hasBookmark){
            System.out.println("You have this restaurant bookmarked, do you want to delete it? y/n");
            String option = Update.getInput();
            if(option.equals("y")){
                deleteBookmark(conn,userId);
            }
        }else{
            System.out.println("You have not mark this restaurant, do you want to add a bookmark? y/n");
            String option = Update.getInput();
            if(option.equals("y")){
                System.out.println("Please input bookmark description:");
                String content = Update.getInput();
                Bookmark.bookmarkInsert(conn,userId,restaurantId,content);
            }
        }
    }

    //edit OK
    private static void outputReviewByUserAndRestaurant(Connection conn,int restaurantId, int userId)  throws SQLException {
        try (CallableStatement statement = conn.prepareCall("{call select_restaurant_review(?)}");) {
            statement.setInt(1, restaurantId);
            ResultSet res = statement.executeQuery();
            while(res.next()) {
                if(res.getInt(2)==userId) {
                System.out.print("Restaurant Id:"+res.getString(1));
                System.out.println(" Review Content:"+res.getString(3));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return ;
        }
        return;
    }

    //edit OK
    private static void outputMenuByRestaurantId(Connection conn, int restaurantId) throws SQLException {
        try (CallableStatement statement = conn.prepareCall("{call select_restaurant_menu(?)}");) {
            statement.setInt(1, restaurantId);
            ResultSet res = statement.executeQuery();
            while(res.next()) {
                System.out.print("Menu Name:"+res.getString(2));
                System.out.println(" Menu Description:"+res.getString(3));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return ;
        }
        return;
    }

    //edit OK
    private static void outputReviewsByRestaurantId(Connection conn,int restaurantId)throws SQLException {
        try (CallableStatement statement = conn.prepareCall("{call select_restaurant_review(?)}");) {
            statement.setInt(1, restaurantId);
            ResultSet res = statement.executeQuery();
            while(res.next()) {

                System.out.print("Restaurant Id:"+res.getString(1));
                System.out.print(" User Id:"+res.getString(2));
                System.out.println(" Review Content:"+res.getString(3));

            }
        } catch (SQLException e) {
            e.printStackTrace();
            return ;
        }

        return;
    }


    public static void editReview(Connection conn,int askId) throws IOException, SQLException {
        System.out.println("Please input the review number you want to edit:");
        String number = Update.getInput();
        if(checkPermitReview(conn,askId,number)) {
            System.out.println("Please input new review:");
            String newReview = Update.getInput();
            Review.edit(conn, Integer.valueOf(number), newReview);
        }
    }

    //edit ：check number在不在askid的reviewid里面
    private static boolean checkPermitReview(Connection conn,int askId, String number) throws SQLException {
        try (CallableStatement statement = conn.prepareCall("{call select_user_review(?)}");) {
            statement.setInt(1, askId);
            ResultSet res = statement.executeQuery();
            while (res.next()) {
                if (res.getString(1).equals(number))
                    return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false ;
        }
        return false;
    }

    public static void deleteReview(Connection conn,int askId) throws SQLException, IOException {
        System.out.println("Please input the review number you want to delete:");
        String number = Update.getInput();
        if(checkPermitReview(conn,askId,number)) {
            Review.delete(conn, Integer.parseInt(number));
        }else{
            System.out.println("You do not have this review.");
        }
    }

    public static void deleteBookmark(Connection conn,int askId) throws IOException, SQLException {
        System.out.println("Please input the bookmark number you want to delete:");
        String number = Update.getInput();
        if(checkPermitBookmark(conn,askId,number)) {
            Bookmark.delete(conn, Integer.parseInt(number));
        }else{
            System.out.println("You do not have this bookmark.");
        }
    }

    //edit ：check number在不在askid的bookmarkid里面
    private static boolean checkPermitBookmark(Connection conn, int askId, String number) throws SQLException {
            try (CallableStatement statement = conn.prepareCall("{call select_user_bookmark(?)}");) {
                statement.setInt(1, askId);
                ResultSet res = statement.executeQuery();
                while (res.next()) {
                    if (res.getString(1).equals(number))
                        return true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return false ;
            }
            return false;
    }

    private static void deleteOrder(Connection conn,int askId) throws IOException, SQLException {
        System.out.println("Please input the order number you want to delete:");
        String number = Update.getInput();
        if(checkPermitOrder(conn,askId,number)) {
            Order.delete(conn, Integer.parseInt(number));
        }else{
            System.out.println("You do not have this order.");
        }
    }

    //edit ：check number在不在askid的orderid里面
    private static boolean checkPermitOrder(Connection conn, int askId, String number) throws SQLException {
        try (CallableStatement statement = conn.prepareCall("{call select_user_order(?)}");) {
            statement.setInt(1, askId);
            ResultSet res = statement.executeQuery();
            while (res.next()) {
                if (res.getString(1).equals(number))
                    return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false ;
        }
        return false;
    }






    // 一个输出的例子
    public void outPutTrack(Connection conn, String name) throws SQLException {

        String sql = "{CALL track_character(?)}";

        ResultSet resultSet = null;
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql);) {

            //name对应上面String sql = "{CALL track_character(?)}"; 中应该传入procedure的?位置的值
            preparedStatement.setString(1, name);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                System.out.println(String.format(
                    "Character_name: %s, Region name: %s, Book name: %s, Encountered character: %s",
                    resultSet.getString(1),
                    resultSet.getString(2),
                    resultSet.getString(3),
                    resultSet.getString(4)));
                System.out.println("Output Finished!");
            }
        } catch (SQLException ex) {
            System.out.println("Run Output Track Query Error");
            ex.printStackTrace();
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
        }
    }


    //一个得到结果集合的例子
    public ArrayList<String> getList(Connection conn,String name) throws SQLException {
        ResultSet resultSet = null;
        String sql = "{CALL track_character(?)}";
        ArrayList<String> result = new ArrayList<>();

        try (PreparedStatement preparedStatement = conn.prepareStatement(sql);) {
            //name对应上面String sql = "{CALL track_character(?)}"; 中应该传入procedure的?位置的值
            preparedStatement.setString(1, name);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String res = resultSet.getString("character_name");
                result.add(name);
            }
        } catch (SQLException ex) {
            System.out.println("Run Query Error");
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
        }
        return result;
    }




}
