package cs.ycp.edu.cs481.ratemydrink.controllers.web_controllers;

import android.os.AsyncTask;
import android.util.Log;

import com.rateMyDrink.modelClasses.User;

import cs.ycp.edu.cs481.ratemydrink.RETROFIT;
import cs.ycp.edu.cs481.ratemydrink.URLInfo;
import retrofit.RestAdapter;

/**
 * Created by Josh on 5/8/2015.
 */
public class LoginUserAsync extends AsyncTask<String, Void, User> {
    @Override
    protected User doInBackground(String... params) {

        IUserRequests getUser = RETROFIT.getRestAdapterBuilder()
                .setEndpoint(URLInfo.DOMAIN_URL)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setLog(new RestAdapter.Log() {
                    @Override
                    public void log(String message) {
                        Log.d("retrofit", message);
                    }
                })
                .build()
                .create(IUserRequests.class);

        return getUser.login(params[0], params[1]);

    }
}
