package cs.ycp.edu.cs481.ratemydrink.activities;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import cs.ycp.edu.cs481.ratemydrink.R;
import cs.ycp.edu.cs481.ratemydrink.fragements.BeerDetailFragment;
import cs.ycp.edu.cs481.ratemydrink.fragements.DrinkListFragment;
import cs.ycp.edu.cs481.ratemydrink.fragements.TypeListFragment;

public class TypeActivity extends ActionBarActivity implements TypeListFragment.Callbacks{
    private String[] navTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private boolean mTwoPane;
    private CharSequence mTitle;
    private CharSequence mDrawerTitle;
    private ActionBarDrawerToggle mDrawerToggle;
    public static boolean loginStatus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type);

        if (findViewById(R.id.drink_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
              ((TypeListFragment) getSupportFragmentManager()
              .findFragmentById(R.id.type_list))
             .setActivateOnItemClick(true);
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

        // TODO: If exposing deep links into your app, handle intents here.
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_type, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        //start the activity to add a beer
        if(id == R.id.newDrink){
            Intent newDrinkIntent = new Intent(this, AddBeerActivity.class);
            startActivity(newDrinkIntent);
            return true;
        }
        //Start the activity to login or register
        if(id == R.id.login){
            Intent newLoginIntent = new Intent(this, LoginActivity.class);
            startActivity(newLoginIntent);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(String id) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            DrinkListFragment fragment = new DrinkListFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.drink_detail_container, fragment)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, DrinkListActivity.class);
            detailIntent.putExtra(BeerDetailFragment.ARG_ITEM_ID, id);
            DrinkListFragment.DRINK_TYPE_ID = Integer.parseInt(id);
            startActivity(detailIntent);
        }
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
        //Main menu screen, but you are already here
        if(position == 4) {
            if(!this.getClass().equals(TypeActivity.class)){
                Intent mainMenuIntent = new Intent(this, TypeActivity.class);
                startActivity(mainMenuIntent);
            }
        }
        //This would go to the profile page
        if(position == 5) {
            if(TypeActivity.loginStatus) {
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
