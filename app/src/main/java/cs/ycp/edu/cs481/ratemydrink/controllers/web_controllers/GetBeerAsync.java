package cs.ycp.edu.cs481.ratemydrink.controllers.web_controllers;


import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.bind.DateTypeAdapter;
import com.rateMyDrink.modelClasses.Drink;

import cs.ycp.edu.cs481.ratemydrink.URLInfo;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * A controller to asynchronously make a GET request to the database for a single drink.
 */
public class GetBeerAsync extends AsyncTask<Integer, Void, Drink> {

    @Override
    protected Drink doInBackground(Integer... params) {

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Drink.class, new DateTypeAdapter())
                .create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(URLInfo.DOMAIN_URL)
                .setConverter(new GsonConverter(gson))
                .build();

        IGetBeer getDrinkService = restAdapter.create(IGetBeer.class);
        getDrinkService.get(params[0]);

        return null;
    }

}