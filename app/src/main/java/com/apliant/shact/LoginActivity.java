package com.apliant.shact;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.apliant.shact.models.User;
import com.apliant.shact.network.VolleySingleton;
import com.apliant.shact.utils.Login;
import com.apliant.shact.utils.UrlEndpoints;
import com.apliant.shact.utils.UrlEndpoints.*;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_login)
public class LoginActivity extends RoboActivity implements View.OnClickListener {
    Login login;
    RequestQueue requestQueue = VolleySingleton.getInstance().getRequestQueue();
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    private static final String TAG = "TAG";
    @InjectView(R.id.loginUsername)
    EditText loginUsername;
    @InjectView(R.id.loginPassword)
    EditText loginPassword;
    @InjectView(R.id.goToRegister)
    TextView goToRegister;
    @InjectView(R.id.buttonLogin)
    Button buttonLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        login = new Login();

        buttonLogin.setOnClickListener(this);
        goToRegister.setOnClickListener(this);

        MyApplication app = (MyApplication) getApplicationContext();
        app.setCurrentUser(null);

        pref = getApplicationContext().getSharedPreferences("ShactPreferences", 0); // 0 - for private mode
        editor = pref.edit();

        editor.putString("token", null);
        editor.commit();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonLogin:
                Log.i("TAG", "BUTTON!");
                String mUsername = loginUsername.getText().toString();
                String mPassword = loginPassword.getText().toString();
                JsonObjectRequest request = login.login(mUsername, mPassword, new Login.LoginCallback() {
                    @Override
                    public void onSuccess(String token, User user) {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        editor.putString("token", token);
                        editor.commit();
                    }

                    @Override
                    public void onError(Login.LoginError error) {
                        error.printStackTrack(TAG);
                    }
                });

                requestQueue.add(request);
                break;
            case R.id.goToRegister:
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                break;
        }
    }
}
