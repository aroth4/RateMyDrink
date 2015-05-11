package cs.ycp.edu.cs481.ratemydrink.controllers.web_controllers;

import android.os.AsyncTask;
import android.util.Log;

import com.rateMyDrink.modelClasses.Drink;

import cs.ycp.edu.cs481.ratemydrink.RETROFIT;
import cs.ycp.edu.cs481.ratemydrink.URLInfo;
import retrofit.RestAdapter;

/**
 * AsyncTask to send a new rating to the backend so a new average rating can be computed.
 */
public class UpdateRatingAsync extends AsyncTask<Drink, Void, Drink> {
    @Override
    protected Drink doInBackground(Drink... params) {
//        Gson gson = new GsonBuilder()
//                .registerTypeAdapter(Drink.class, new DrinkAdapter())
//                .create();

        IGetDrinksList updateRating = RETROFIT.getRestAdapterBuilder()
                .setEndpoint(URLInfo.DOMAIN_URL)
                //.setConverter(new GsonConverter(gson))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setLog(new RestAdapter.Log() {
                    @Override
                    public void log(String message) {
                        Log.d("retrofit", message);
                    }
                })
                .build()
                .create(IGetDrinksList.class);

        return updateRating.updateRating(params[0], params[0].getRating());
    }
}
