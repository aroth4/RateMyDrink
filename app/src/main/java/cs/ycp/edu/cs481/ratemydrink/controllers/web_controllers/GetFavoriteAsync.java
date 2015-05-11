package cs.ycp.edu.cs481.ratemydrink.controllers.web_controllers;

import android.os.AsyncTask;
import android.util.Log;

import com.rateMyDrink.modelClasses.Drink;
import com.rateMyDrink.modelClasses.Favorite;

import cs.ycp.edu.cs481.ratemydrink.RETROFIT;
import cs.ycp.edu.cs481.ratemydrink.URLInfo;
import retrofit.RestAdapter;

/**
 * Created by Josh on 5/8/2015.
 */
public class GetFavoriteAsync extends AsyncTask<Favorite, Void, Drink> {
    @Override
    protected Drink doInBackground(Favorite... params) {

        IFavoriteRequests getFavoriteDrink = RETROFIT.getRestAdapterBuilder()
                .setEndpoint(URLInfo.DOMAIN_URL)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setLog(new RestAdapter.Log() {
                    @Override
                    public void log(String message) {
                        Log.d("retrofit", message);
                    }
                })
                .build()
                .create(IFavoriteRequests.class);

        return getFavoriteDrink.getFavoriteDrink(params[0].getDrinkId());

    }
}
