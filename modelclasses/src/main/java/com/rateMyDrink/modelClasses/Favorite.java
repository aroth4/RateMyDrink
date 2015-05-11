package com.rateMyDrink.modelClasses;

/**
 * Created by shanembonner on 5/3/15.
 */
public class Favorite {

    private int drinkId;
    private int userId;

    /**
     * default constructor
     */
    public Favorite(){

    }

    /**
     * constructor to instanitate all fields.
     *
     * @param drinkId id for the drink object.
     * @param userId id for the user.
     */
    public Favorite(int drinkId, int userId){
        this.drinkId = drinkId;
        this.userId = userId;
    }

    public int getDrinkId() {
        return drinkId;
    }

    public void setDrinkId(int drinkId) {
        this.drinkId = drinkId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
