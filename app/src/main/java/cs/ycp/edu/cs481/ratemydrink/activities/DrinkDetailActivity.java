package cs.ycp.edu.cs481.ratemydrink.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import cs.ycp.edu.cs481.ratemydrink.R;
import cs.ycp.edu.cs481.ratemydrink.fragements.BeerDetailFragment;
import cs.ycp.edu.cs481.ratemydrink.fragements.DrinkListFragment;
import cs.ycp.edu.cs481.ratemydrink.fragements.LiquorDetailFragment;
import cs.ycp.edu.cs481.ratemydrink.fragements.MixedDrinkFragment;


/**
 * An activity representing a single Drink detail screen. This
 * activity is only used on handset devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link DrinkListActivity}.
 * <p/>
 * This activity is mostly just a 'shell' activity containing nothing
 * more than a {@link BeerDetailFragment}.
 */
public class DrinkDetailActivity extends ActionBarActivity {
    private String[] navTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private boolean mTwoPane;
    private CharSequence mTitle;
    private CharSequence mDrawerTitle;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //If Beer initially was chosen, do this, liquor, do option two, etc.
       // if()
        setContentView(R.layout.activity_drink_detail);

        // Show the Up button in the action bar, rating bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(BeerDetailFragment.ARG_ITEM_ID,
                    getIntent().getStringExtra(BeerDetailFragment.ARG_ITEM_ID));
            //DrinkDetailFragment fragment = new DrinkDetailFragment();
            Fragment fragment = null;
            if(DrinkListFragment.DRINK_TYPE_ID == 1){
                fragment = new BeerDetailFragment();
            }else if(DrinkListFragment.DRINK_TYPE_ID == 2){
                fragment = new LiquorDetailFragment();
            }else{
                fragment = new MixedDrinkFragment();
            }
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.drink_detail_container, fragment)
                    .commit();
        }
        navTitles = getResources().getStringArray(R.array.navItems);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        mDrawerList.setAdapter(new ArrayAdapter<String>(this,R.layout.drawer_list_item, navTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());


//        mDrawerToggle = new ActionBarDrawerToggle(
//                this,                  /* host Activity */
//                mDrawerLayout,         /* DrawerLayout object */
//                R.drawable.drinkicon,  /* nav drawer icon to replace 'Up' caret */
//                R.string.drawer_open,  /* "open drawer" description */
//                R.string.drawer_close  /* "close drawer" description */
//        ) {
//
//            /** Called when a drawer has settled in a completely closed state. */
//            public void onDrawerClosed(View view) {
//                super.onDrawerClosed(view);
//                getActionBar().setTitle(mTitle);
//            }
//
//            /** Called when a drawer has settled in a completely open state. */
//            public void onDrawerOpened(View drawerView) {
//                super.onDrawerOpened(drawerView);
//                getActionBar().setTitle(mDrawerTitle);
//            }
//        };

        // Set the drawer toggle as the DrawerListener
        mDrawerList.setItemChecked(4, true);
        setTitle(navTitles[4]);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //This allows user to go back to drink list page
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpTo(this, new Intent(this, DrinkListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Allows items from drawer to be selected
    private boolean selectItem(int position) {

        //Open up login page
        if(position == 0)
        {
            Intent newLoginIntent = new Intent(this, LoginActivity.class);
            startActivity(newLoginIntent);
            return true;
        }
        //Open up new drink page
        if(position == 1) {
            Intent newDrinkIntent = new Intent(this, AddBeerActivity.class);
            startActivity(newDrinkIntent);
            return true;
        }
        //This would go to add a liquor drink
        if(position == 2) {
            Intent newLiquorIntent = new Intent(this, AddLiquorActivity.class);
            startActivity(newLiquorIntent);
            return true;
        }
        //This would go to add a mixed drink
        if(position == 3) {
            Intent newMixedIntent = new Intent(this, AddMixedDrinkActivity.class);
            startActivity(newMixedIntent);
            return true;
        }
        //Main menu screen
        if(position == 4) {
            Intent newMenuIntent = new Intent(this, TypeActivity.class);
            startActivity(newMenuIntent);
            return true;
        }
        //This would go to the profile page
        if(position == 5) {
            if(TypeActivity.loginStatus == true) {
                Intent newProfile = new Intent(this, ProfileActivity.class);
                startActivity(newProfile);
            }
            else{
                Toast.makeText(this, "You need to login to view a profile!", Toast.LENGTH_SHORT).show();
            }
            return true;

        }
        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(navTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
        return false;
    }

    //Click listener on drawer
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }
}
