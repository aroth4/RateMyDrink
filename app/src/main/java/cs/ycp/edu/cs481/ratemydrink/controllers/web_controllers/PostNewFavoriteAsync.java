package cs.ycp.edu.cs481.ratemydrink.controllers.web_controllers;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rateMyDrink.modelClasses.Comment;
import com.rateMyDrink.modelClasses.Favorite;

import cs.ycp.edu.cs481.ratemydrink.RETROFIT;
import cs.ycp.edu.cs481.ratemydrink.URLInfo;
import cs.ycp.edu.cs481.ratemydrink.controllers.CommentAdapter;
import retrofit.converter.GsonConverter;

/**
 * Created by Josh on 5/10/2015.
 */
public class PostNewFavoriteAsync extends AsyncTask<Favorite, Void, Favorite> {
    @Override
    protected Favorite doInBackground(Favorite... params) {

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Comment.class, new CommentAdapter())
                .create();

        IFavoriteRequests postFavorite = RETROFIT.getRestAdapterBuilder()
                .setEndpoint(URLInfo.DOMAIN_URL)
                .setConverter(new GsonConverter(gson))
                .build()
                .create(IFavoriteRequests.class);

        return postFavorite.postNewFavorite(params[0]);

    }
}
