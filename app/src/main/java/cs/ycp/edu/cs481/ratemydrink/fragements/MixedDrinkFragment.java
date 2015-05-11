package cs.ycp.edu.cs481.ratemydrink.fragements;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.rateMyDrink.modelClasses.Comment;
import com.rateMyDrink.modelClasses.Drink;
import com.rateMyDrink.modelClasses.Favorite;
import com.rateMyDrink.modelClasses.MixedDrink;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import cs.ycp.edu.cs481.ratemydrink.R;
import cs.ycp.edu.cs481.ratemydrink.UserInfo;
import cs.ycp.edu.cs481.ratemydrink.activities.TypeActivity;
import cs.ycp.edu.cs481.ratemydrink.controllers.web_controllers.GetCommentsAsync;
import cs.ycp.edu.cs481.ratemydrink.controllers.web_controllers.GetMixedDrinkAsync;
import cs.ycp.edu.cs481.ratemydrink.controllers.web_controllers.PostNewCommentAsync;
import cs.ycp.edu.cs481.ratemydrink.controllers.web_controllers.PostNewFavoriteAsync;
import cs.ycp.edu.cs481.ratemydrink.controllers.web_controllers.UpdateRatingAsync;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 */
public class MixedDrinkFragment extends Fragment {

    ArrayList<String> comments;
    MixedDrink mMixedDrink;
    
    public MixedDrinkFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int id = -1;

        if (getArguments().containsKey(BeerDetailFragment.ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            id = Integer.valueOf(getArguments().getString(BeerDetailFragment.ARG_ITEM_ID));
        }

        if(id > 0){
            GetMixedDrinkAsync getBeer = new GetMixedDrinkAsync();
            getBeer.execute(id);
            try {
                try {
                    mMixedDrink = getBeer.get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_mixed_drink, container, false);

        if(mMixedDrink != null) {
            ((TextView) v.findViewById(R.id.beer_name)).setText(mMixedDrink.getDrinkName());
            ((TextView) v.findViewById(R.id.beer_desc)).setText(mMixedDrink.getDescription());
            //((TextView) v.findViewById(R.id.beer_type)).setText(mMixedDrink.getMaxIngredientReadableType());
            ((TextView) v.findViewById(R.id.beerAvgRate)).setText(mMixedDrink.getRating() + "");
        }

        final ListView commentsList = (ListView) v.findViewById(R.id.comments_listview);

        comments = new ArrayList<String>();

        GetCommentsAsync getComments = new GetCommentsAsync();
        getComments.execute(mMixedDrink.getId(),0, 10);

        try {
            try{
                Comment[] commentArr = getComments.get();
                if(commentArr != null){
                    for(Comment comment : commentArr){
                        comments.add(comment.getComment());
                    }
                }
            }catch(ExecutionException e){
                e.printStackTrace();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        final ArrayAdapter<String> commentAdapter = new ArrayAdapter<String>(
                getActivity().getBaseContext(), android.R.layout.simple_expandable_list_item_1, comments);

        commentsList.setAdapter(commentAdapter);

        final EditText commentEditText = (EditText) v.findViewById(R.id.comments);

        final Button addComment = (Button) v.findViewById(R.id.button);
        addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commentStr = commentEditText.getText().toString();
                if(!commentStr.equals("")){
                    Comment comment = new Comment(mMixedDrink.getId(), "user", commentStr);
                    comments.add(commentEditText.getText().toString());
                    commentAdapter.notifyDataSetChanged();
                    PostNewCommentAsync postComment = new PostNewCommentAsync();
                    postComment.execute(comment);
                }
            }
        });
        final Button favorites = (Button) v.findViewById(R.id.favButton);
        favorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TypeActivity.loginStatus){
                    Favorite newFavorite = new Favorite(mMixedDrink.getId(), UserInfo.user.getId());
                    PostNewFavoriteAsync postFavorite = new PostNewFavoriteAsync();
                    postFavorite.execute(newFavorite);
                    Toast.makeText(getActivity(), "Drink has been added to your favorites!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        RatingBar ratingBar = (RatingBar) v.findViewById(R.id.BeerRatingBar);
        ratingBar.setRating(mMixedDrink.getRating());
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                UpdateRatingAsync updateAsync = new UpdateRatingAsync();

                Drink drink = new Drink();
                drink.setRating(rating);
                drink.setId(mMixedDrink.getId());

                updateAsync.execute(drink);

//                    try {
//                        drink = updateAsync.get();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    } catch (ExecutionException e) {
//                        e.printStackTrace();
//                    }

                ratingBar.setRating(drink.getRating());
            }
        });
        
        
        return v;

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
