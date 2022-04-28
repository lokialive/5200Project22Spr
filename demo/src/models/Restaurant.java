package models;


public class Restaurant {


    private int restaurantId;
    private String restaurantName;
    private String restaurantPhoneNumber;
    private String restaurantAddress;
    private String city;
    private String state;
    private String zipcode;

    public Restaurant() {
    }

    public Restaurant(int restaurantId, String restaurantName, String restaurantPhoneNumber,
        String restaurantAddress, String city, String state, String zipcode) {
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
        this.restaurantPhoneNumber = restaurantPhoneNumber;
        this.restaurantAddress = restaurantAddress;
        this.city = city;
        this.state = state;
        this.zipcode = zipcode;
    }

    public int getRestaurantId() {
        return restaurantId;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public String getRestaurantPhoneNumber() {
        return restaurantPhoneNumber;
    }

    public String getRestaurantAddress() {
        return restaurantAddress;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getZipcode() {
        return zipcode;
    }
}
