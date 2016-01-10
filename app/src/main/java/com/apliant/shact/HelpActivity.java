package com.apliant.shact;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.apliant.shact.utils.SetupUI;

public class HelpActivity extends BaseActivity {
    Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        getSupportActionBar().setTitle("Ajuda");
        mDrawer.setSelection(DRAWER_HELP, false);
    }
}
