package com.apliant.shact;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.apliant.shact.fragments.ContactsFragment;
import com.apliant.shact.fragments.RecentsFragment;
import com.apliant.shact.fragments.SelfieFragment;
import com.apliant.shact.views.adapters.ViewPagerAdapter;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.context.IconicsContextWrapper;


public class MainActivity extends BaseActivity {

    TabLayout mTabLayout;
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Shact");
        mDrawer.setSelection(DRAWER_HOME, false);

        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);

        setupViewPager();
        setupTabs();
    }

    private void setupViewPager() {
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new RecentsFragment().newInstance(0), getResources().getString(R.string.tab_recents));
        adapter.addFragment(new SelfieFragment().newInstance(0), getResources().getString(R.string.tab_selfie));
        adapter.addFragment(new ContactsFragment().newInstance(0), getResources().getString(R.string.tab_contacts));
        mViewPager.setAdapter(adapter);
    }

    private void setupTabs() {
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.getTabAt(0).setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_people).color(Color.WHITE).actionBar());
        mTabLayout.getTabAt(1).setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_camera_alt).color(Color.WHITE).actionBar());
        mTabLayout.getTabAt(2).setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_notifications).color(Color.WHITE).actionBar());
    }





    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(IconicsContextWrapper.wrap(newBase));
    }

}
