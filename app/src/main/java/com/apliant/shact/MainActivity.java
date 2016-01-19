package com.apliant.shact;

import android.animation.Animator;
import android.app.ActionBar;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Camera;
import android.media.Image;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.apliant.shact.fragments.ContactsFragment;
import com.apliant.shact.fragments.RecentsFragment;
import com.apliant.shact.fragments.SelfieFragment;
import com.apliant.shact.views.adapters.ViewPagerAdapter;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.context.IconicsContextWrapper;

import net.bozho.easycamera.DefaultEasyCamera;
import net.bozho.easycamera.EasyCamera;

import java.io.IOException;


public class MainActivity extends BaseActivity {
    SelfieFragment selfieFragment;
    TabLayout mTabLayout;
    ViewPager mViewPager;
    AppBarLayout mAppBarLayout;
    Toolbar mBottomToolbar;
    LinearLayout mLinearLayout;
    Boolean cameraFacingFront = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Shact");
        mDrawer.setSelection(DRAWER_HOME, false);

        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        mBottomToolbar = (Toolbar) findViewById(R.id.toolbar_bottom);
        mLinearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        ImageView mCameraShutter = (ImageView) findViewById(R.id.camera_shutter);
        ImageView mCameraDisplay = (ImageView) findViewById(R.id.camera_display);

        mCameraShutter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("TAG", "CLICK!");
                //TODO: take picture
            }
        });
        mCameraDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selfieFragment.getmCamera().stopPreview();  // stop preview
                selfieFragment.getmCamera().close();        //close camera
                if (cameraFacingFront){                     //im in facing front mode
                    cameraFacingFront = false;
                    selfieFragment.setmCamera(DefaultEasyCamera.open(Camera.CameraInfo.CAMERA_FACING_BACK));
                }else {                                     //im in facing back mode
                    cameraFacingFront = true;
                    selfieFragment.setmCamera(DefaultEasyCamera.open(Camera.CameraInfo.CAMERA_FACING_FRONT));
                }
                selfieFragment.getmCamera().setDisplayOrientation(90);
                selfieFragment.startStream();
                selfieFragment.resizeSurface();
            }
        });

        setupViewPager();
        setupTabs();
    }

    private void setupViewPager() {
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        selfieFragment =  new SelfieFragment().newInstance(0);
        adapter.addFragment(new RecentsFragment().newInstance(0), getResources().getString(R.string.tab_recents));
        adapter.addFragment(selfieFragment, getResources().getString(R.string.tab_selfie));
        adapter.addFragment(new ContactsFragment().newInstance(0), getResources().getString(R.string.tab_contacts));


        mViewPager.setAdapter(adapter);
    }


    @Override
    protected void onPause() {
        Log.i("TAG", "PUASE");
        //selfieFragment.getmCamera().stopPreview();
        //selfieFragment.getmCamera().close();
        super.onPause();
    }


    private void setupTabs() {
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.getTabAt(0).setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_people).color(Color.WHITE).actionBar());
        mTabLayout.getTabAt(1).setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_camera_alt).color(Color.WHITE).actionBar());
        mTabLayout.getTabAt(2).setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_notifications).color(Color.WHITE).actionBar());
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.i("TAG", Integer.toString(tab.getPosition()));
                if (tab.getPosition() == 1){
                    getSupportActionBar().setShowHideAnimationEnabled(true);
                    Animation fadeIn = new AlphaAnimation(0, 1);
                    fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
                    fadeIn.setDuration(1000);


                    mAppBarLayout.animate().translationY(-mToolbar.getHeight())
                            .setDuration(500)
                            .setInterpolator(new DecelerateInterpolator()).start();
                    mViewPager.animate().translationY(-mToolbar.getHeight())
                            .setDuration(500)
                            .setInterpolator(new DecelerateInterpolator())
                            .start();
                    mBottomToolbar.animate().translationY(-mToolbar.getHeight())
                            .setDuration(500)
                            .setInterpolator(new DecelerateInterpolator())
                            .start();



                    //LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    //mViewPager.setLayoutParams(layoutParams);
                }else{
                    mAppBarLayout.animate().translationY(0)
                            .setDuration(500)
                            .start();
                    mViewPager.animate().translationY(0)
                            .setDuration(500)
                            .start();
                    mBottomToolbar.animate().translationY(0)
                            .setDuration(500)
                            .setInterpolator(new DecelerateInterpolator())
                            .start();
                }
                mViewPager.setCurrentItem(tab.getPosition(), true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }





    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(IconicsContextWrapper.wrap(newBase));
    }

}
