package cs.ycp.edu.cs481.ratemydrink.fragements;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

import cs.ycp.edu.cs481.ratemydrink.R;
import cs.ycp.edu.cs481.ratemydrink.UserInfo;
import cs.ycp.edu.cs481.ratemydrink.activities.RegisterActivity;
import cs.ycp.edu.cs481.ratemydrink.activities.TypeActivity;
import cs.ycp.edu.cs481.ratemydrink.controllers.web_controllers.LoginUserAsync;


/**
 * Created by Aaron on 3/19/2015.
 */
public class LoginFragment extends Fragment {

    private EditText username, password;
    private Button submit, register;

    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        username = (EditText) rootView.findViewById(R.id.username_field);
        password = (EditText) rootView.findViewById(R.id.password_field);

        submit = (Button) rootView.findViewById(R.id.submitLogin);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UserInfo.user = null;

                LoginUserAsync loginUser = new LoginUserAsync();
                loginUser.execute(username.getText().toString(), password.getText().toString());

                try {
                    UserInfo.user = loginUser.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                if(UserInfo.user != null){
                    Toast.makeText(getActivity(), "logged in!", Toast.LENGTH_SHORT).show();
                    TypeActivity.loginStatus = true;
                    Intent typeIntent = new Intent(getActivity().getBaseContext(), TypeActivity.class);
                    startActivity(typeIntent);
                }else{
                    Log.d("NULL USER", "login failed.");
                }

            }
        });

        register = (Button) rootView.findViewById(R.id.signUp);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent;
                registerIntent = new Intent(getActivity(), RegisterActivity.class);
                startActivity(registerIntent);
            }
        });


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
