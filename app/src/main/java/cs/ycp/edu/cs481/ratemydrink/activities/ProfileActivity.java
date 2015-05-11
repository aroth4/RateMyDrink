package cs.ycp.edu.cs481.ratemydrink.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import cs.ycp.edu.cs481.ratemydrink.R;
import cs.ycp.edu.cs481.ratemydrink.TabListener;
import cs.ycp.edu.cs481.ratemydrink.fragements.FavoritesFragment;
import cs.ycp.edu.cs481.ratemydrink.fragements.ProfileFragment;

@SuppressWarnings("deprecation")
public class ProfileActivity extends ActionBarActivity {

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
        // setup action bar for tabs
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(false);

        ActionBar.Tab tab = actionBar.newTab()
                .setText(R.string.ProfileTab1)
                .setTabListener(new TabListener<ProfileFragment>(
                        this, "Profile", ProfileFragment.class));
        actionBar.addTab(tab);

        ActionBar.Tab tab2 = actionBar.newTab()
                .setText(R.string.ProfileTab2)
                .setTabListener(new TabListener<FavoritesFragment>(
                        this, "Favorites", FavoritesFragment.class));
        actionBar.addTab(tab2);

//        ActionBar.Tab tab3 = actionBar.newTab()
//                .setText(R.string.ProfileTab3)
//                .setTabListener(new TabListener<CommentsFragment>(
//                        this, "Comments", CommentsFragment.class));
//        actionBar.addTab(tab3);

//        setContentView(R.layout.activity_profile);
//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.Pcontainer, new ProfileFragment())
//                    .commit();
//        }
//        navTitles = getResources().getStringArray(R.array.navItems);
//        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
//        mDrawerList = (ListView) findViewById(R.id.left_drawer);
//
//        mDrawerList.setAdapter(new ArrayAdapter<String>(this,R.layout.drawer_list_item, navTitles));
//        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
//
//        // Set the drawer toggle as the DrawerListener
//        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void setupTabs() {
//        ActionBar bar = getSupportActionBar();
//        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
//        bar.setDisplayShowTitleEnabled(true);
//
//        //Add Tabs
//        ActionBar.Tab profileTab = bar
//                .newTab()
//                .setText("Info")
//                .setIcon(R.drawable.drinkicon)
//                .setTabListener(
//                        new SupportFragmentTabListener<ProfileFragment>(R.id.), this, "Profile", ProfileFragment.class));
//

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_profile, menu);
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
        //Main menu screen, but you are already here
        if(position == 4) {
            Intent newMenuIntent = new Intent(this, TypeActivity.class);
            startActivity(newMenuIntent);
            return true;
        }
        // Highlight the selected item, update the title, and close the drawer
//        mDrawerList.setItemChecked(position, true);
//        setTitle(navTitles[position]);
//        mDrawerLayout.closeDrawer(mDrawerList);
        return false;
    }

    //Click listener on drawer
//    private class DrawerItemClickListener implements ListView.OnItemClickListener {
//        @Override
//        public void onItemClick(AdapterView parent, View view, int position, long id) {
//            selectItem(position);
//        }
//    }

}