package models;


public class Menu {

    private int menuId;
    private String menuName;
    private String menuDes;
    private int restaurantId;

    public Menu() {
    }

    public Menu(int menuId, String menuName, String menuDes, int restaurantId) {
        this.menuId = menuId;
        this.menuName = menuName;
        this.menuDes = menuDes;
        this.restaurantId = restaurantId;
    }

    public int getMenuId() {
        return menuId;
    }

    public String getMenuName() {
        return menuName;
    }

    public String getMenuDes() {
        return menuDes;
    }

    public int getRestaurantId() {
        return restaurantId;
    }
}
