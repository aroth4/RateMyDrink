package cs.ycp.edu.cs481.ratemydrink.controllers.web_controllers;

import com.rateMyDrink.modelClasses.Beer;
import com.rateMyDrink.modelClasses.Drink;
import com.rateMyDrink.modelClasses.Liquor;
import com.rateMyDrink.modelClasses.MixedDrink;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * An interface using the Retrofit api to make RESTful request to the server-side database for a list of drinks
 */
public interface IGetDrinksList {

    @GET("/backend/?action=getDrinkList")
    Drink[] getDrinkList();

    @GET("/backend/?action=getBeerList")
    Beer[] getBeerList();

    @GET("/backend/?action=getLiquorList")
    Liquor[] getLiquorList();

    @GET("/backend/?action=getMixedDrinkList")
    MixedDrink[] getMixedDrinkList();

    @POST("/backend/?action=updateRating")
    Drink updateRating(@Body Drink drink, @Query("rating") float rating);

}
