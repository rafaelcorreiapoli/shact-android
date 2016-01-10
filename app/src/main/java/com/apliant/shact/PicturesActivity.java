package com.apliant.shact;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.apliant.shact.BaseActivity;
import com.apliant.shact.MyApplication;
import com.apliant.shact.R;
import com.apliant.shact.utils.SetupUI;

public class PicturesActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pictures);

        getSupportActionBar().setTitle("Minhas Fotos");
        mDrawer.setSelection(DRAWER_PICTURES, false);
    }
}
