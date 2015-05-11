package cs.ycp.edu.cs481.ratemydrink.fragements;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.concurrent.ExecutionException;

import cs.ycp.edu.cs481.ratemydrink.R;
import cs.ycp.edu.cs481.ratemydrink.UserInfo;
import cs.ycp.edu.cs481.ratemydrink.controllers.web_controllers.GetFavoritesListAsync;

/**
 * Created by Aaron on 4/16/2015.
 */
public class ProfileFragment extends Fragment {
    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private TextView username, drinks, favorites, comments;
    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GetFavoritesListAsync favoritesListAsync = new GetFavoritesListAsync();
        favoritesListAsync.execute(UserInfo.user.getId());

        UserInfo.favorites = null;

        try {
            UserInfo.favorites = favoritesListAsync.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        //Init field IDs
        username = (TextView) rootView.findViewById(R.id.usernameField);
        favorites = (TextView) rootView.findViewById(R.id.totalFavoritesField);

        //Set field IDs to their respective database values
        //TEMP: Using pre-set values

        username.setText(UserInfo.user.getUserName());
        //When pulling number from the database, toString will be needed for following

        favorites.setText(String.valueOf(UserInfo.favorites.length));

        return rootView;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            //setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}

