package cs.ycp.edu.cs481.ratemydrink.fragements;

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

import com.rateMyDrink.modelClasses.Beer;
import com.rateMyDrink.modelClasses.Comment;
import com.rateMyDrink.modelClasses.Drink;
import com.rateMyDrink.modelClasses.Favorite;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import cs.ycp.edu.cs481.ratemydrink.R;
import cs.ycp.edu.cs481.ratemydrink.UserInfo;
import cs.ycp.edu.cs481.ratemydrink.activities.TypeActivity;
import cs.ycp.edu.cs481.ratemydrink.controllers.web_controllers.GetBeerAsync;
import cs.ycp.edu.cs481.ratemydrink.controllers.web_controllers.GetCommentsAsync;
import cs.ycp.edu.cs481.ratemydrink.controllers.web_controllers.PostNewCommentAsync;
import cs.ycp.edu.cs481.ratemydrink.controllers.web_controllers.PostNewFavoriteAsync;
import cs.ycp.edu.cs481.ratemydrink.controllers.web_controllers.UpdateRatingAsync;

/**
 * A fragment representing a single Drink detail screen.
 * This fragment is either contained in a {@link cs.ycp.edu.cs481.ratemydrink.activities.DrinkListActivity}
 * in two-pane mode (on tablets) or a {@link cs.ycp.edu.cs481.ratemydrink.activities.DrinkDetailActivity}
 * on handsets.
 */
public class BeerDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private Beer mBeer;                 //beer object to be posted to the database
    private TextView txtRatingValue;
    private ArrayList<String> comments;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BeerDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int id = -1;

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            id = Integer.valueOf(getArguments().getString(ARG_ITEM_ID));
        }

        if(id > 0){
            GetBeerAsync getBeer = new GetBeerAsync();
            getBeer.execute(id);
            try {
                try {
                    mBeer = getBeer.get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_beer_drink_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mBeer != null) {
            ((TextView) rootView.findViewById(R.id.beer_name)).setText(mBeer.getDrinkName());
            ((TextView) rootView.findViewById(R.id.beer_desc)).setText(mBeer.getDescription());
            ((TextView) rootView.findViewById(R.id.beer_abv)).setText("ABV: " + mBeer.getABV());
            ((TextView) rootView.findViewById(R.id.beer_type)).setText(mBeer.getBeerTypeReadableName());
            ((TextView) rootView.findViewById(R.id.beerAvgRate)).setText(mBeer.getRating() + "");

            final ListView commentsList = (ListView) rootView.findViewById(R.id.comments_listview);

            comments = new ArrayList<String>();

            GetCommentsAsync getComments = new GetCommentsAsync();
            getComments.execute(mBeer.getId(), 0, 10);

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

            final EditText commentEditText = (EditText) rootView.findViewById(R.id.comments);

            final Button addComment = (Button) rootView.findViewById(R.id.button);
            addComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String commentStr = commentEditText.getText().toString();
                    if (!commentStr.equals("")) {
                        comments.add(commentStr);
                        commentEditText.setText("");
                        PostNewCommentAsync postComment = new PostNewCommentAsync();
                        postComment.execute(new Comment(mBeer.getId(), "user", commentStr));
                        //commentAdapter.notifyDataSetChanged();
                    }
                }
            });
            final Button favorites = (Button) rootView.findViewById(R.id.favButton);
            favorites.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (TypeActivity.loginStatus) {
                        PostNewFavoriteAsync postFavorite = new PostNewFavoriteAsync();
                        postFavorite.execute(new Favorite(mBeer.getId(), UserInfo.user.getId()));
                        Toast.makeText(getActivity(), "Drink has been added to your favorites!", Toast.LENGTH_SHORT).show();
                    }
                    }
                }
                );

                final RatingBar ratingBar = (RatingBar) rootView.findViewById(R.id.BeerRatingBar);
                ratingBar.setRating(mBeer.getRating());
                ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                   @Override
                   public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                       UpdateRatingAsync updateAsync = new UpdateRatingAsync();

                       Drink drink = new Drink();
                       drink.setRating(rating);
                       drink.setId(mBeer.getId());

                       updateAsync.execute(drink);
                   }
               }

                );
            }

            return rootView;
    }
}
