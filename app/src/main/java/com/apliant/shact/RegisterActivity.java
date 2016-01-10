package com.apliant.shact;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_register)
public class RegisterActivity extends RoboActivity implements View.OnClickListener {
    @InjectView(R.id.registerName) EditText registerName;
    @InjectView(R.id.registerUsername) EditText registerUsername;
    @InjectView(R.id.registerPassword) EditText registerPassword;
    @InjectView(R.id.registerPasswordConfirmation) EditText registerPasswordConfirmation;
    @InjectView(R.id.buttonRegister) Button buttonRegister;
    @InjectView(R.id.goToLogin) TextView goToLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonRegister:
                break;
            case R.id.goToLogin:
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                break;
        }
    }
}
