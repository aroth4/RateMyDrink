package cs.ycp.edu.cs481.ratemydrink.controllers.web_controllers;

import android.os.AsyncTask;
import android.util.Log;

import com.rateMyDrink.modelClasses.Drink;

import cs.ycp.edu.cs481.ratemydrink.RETROFIT;
import cs.ycp.edu.cs481.ratemydrink.URLInfo;
import retrofit.RestAdapter;

/**
 * A controller to asynchronously make a GET request to the database for an array of Drink objects.
 */
public class GetDrinkListAsync extends AsyncTask<Integer, Void, Drink[]>{

    @Override
    protected Drink[] doInBackground(Integer... params) {

        IGetDrinksList getDrinkListService = RETROFIT.getRestAdapterBuilder()
                .setEndpoint(URLInfo.DOMAIN_URL)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setLog(new RestAdapter.Log() {
                    @Override
                    public void log(String message) {
                        Log.d("retrofit", message);
                    }
                })
                .build()
                .create(IGetDrinksList.class);

        if(params[0] == 1){
            return getDrinkListService.getBeerList();
        }else if(params[0] == 2){
            return getDrinkListService.getLiquorList();
        }else{
            return getDrinkListService.getMixedDrinkList();
        }

    }

}
