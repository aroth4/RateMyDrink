package cs.ycp.edu.cs481.ratemydrink.activities;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.rateMyDrink.modelClasses.BeerType;

import java.util.ArrayList;

import cs.ycp.edu.cs481.ratemydrink.R;

public class AddBeerActivity extends ActionBarActivity implements OnClickListener {
        Spinner spinner;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_beer);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        //listenOnSpinnerSelection();
        spinner = (Spinner) findViewById((R.id.new_beer_spinner));

        //I don't know why this isn't working - getting null pointer exception. it SHOULD be getting string values right from ENUM list
        //Spinner spinner = (Spinner) findViewById(R.id.new_beer_spinner);
        //spinner.setAdapter(new ArrayAdapter<BeerType>(this, android.R.layout.simple_spinner_item, BeerType.values()));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_beer, menu);
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

    @Override
    public void onClick(View v) {
        //Insert database calls here, otherwise toast for now

    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
            Spinner spinner;
            Button submit;
        public PlaceholderFragment() {


        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_add_beer, container, false);

            submit = (Button) rootView.findViewById(R.id.add_beer);

            return rootView;
        }
    }
}


