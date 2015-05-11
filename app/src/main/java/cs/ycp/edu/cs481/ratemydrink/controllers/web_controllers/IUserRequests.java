package cs.ycp.edu.cs481.ratemydrink.controllers.web_controllers;

import com.rateMyDrink.modelClasses.User;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * A simple interface using the Retrofit api to make RESTful requests register and login a user with the backend database
 */
public interface IUserRequests {

    /**
     *registers a new user with the backend
     *
     * @param newUser User object containing the new user's username & password
     * @return User object containing user's information
     */
    @Headers({"Content-Type: application/json"})
    @POST("/backend/?action=addUser")
    User register(@Body User newUser);

    /**
     * logs an existing user into the backend.
     *
     * @param password the user's password
     * @return User object containing the user's information
     */
    @GET("/backend/?action=getUser")
    User login(@Query("username") String username, @Query("password") String password);

}
