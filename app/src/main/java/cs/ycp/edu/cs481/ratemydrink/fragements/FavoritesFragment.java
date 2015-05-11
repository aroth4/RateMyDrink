package cs.ycp.edu.cs481.ratemydrink.fragements;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import cs.ycp.edu.cs481.ratemydrink.R;
import cs.ycp.edu.cs481.ratemydrink.UserInfo;

/**
 * Created by Aaron on 4/20/2015.
 */
public class FavoritesFragment extends Fragment {
    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";
    private ListView favs;
    private String[] list2;
    private ArrayAdapter<String> adapter2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile_drinks, container, false);
        //Init listview
        list2 = getResources().getStringArray(R.array.tempFavs);
        favs = (ListView) rootView.findViewById(R.id.favoritesList);

        String[] str = new String[UserInfo.favorites.length];
        for(int i = 0; i < str.length; i++){
            str[i] = UserInfo.favorites[i].getDrinkName(); //String.valueOf(UserInfo.favorites[i]);
        }

        //Set ListView (temp for now)
        adapter2 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, str);
        favs.setAdapter(adapter2);

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


