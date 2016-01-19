package com.apliant.shact;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.apliant.shact.models.User;
import com.apliant.shact.network.VolleySingleton;
import com.apliant.shact.utils.Login;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class BaseActivity extends AppCompatActivity {
    private static final String PREFERENCES_NAME = "ShactPreferences";
    protected static final String TAG = "TAG";
    protected static final Integer DRAWER_HOME = 1;
    protected static final Integer DRAWER_MY_PROFILE = 2;
    protected static final Integer DRAWER_NETWORKS = 3;
    protected static final Integer DRAWER_PICTURES = 4;
    protected static final Integer DRAWER_HELP = 5;
    protected static final Integer DRAWER_LOGOUT = 6;

    Toolbar mToolbar;
    MyApplication app;
    Login mLoginRequest;
    AccountHeader mDrawerHeader;
    Drawer mDrawer;
    final RequestQueue mRequestQueue = VolleySingleton.getInstance().getRequestQueue();

    SharedPreferences mPref;
    SharedPreferences.Editor mEditor;


    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setupBaseUI();

        if (app.getCurrentUser() != null) {
            setupDrawerProfile();
        } else {
            loginWithToken(new Login.LoginCallback() {
                @Override
                public void onSuccess(String token, User user) {
                    app.setCurrentUser(user);
                    app.getCurrentUser().downloadAvatar(new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap response) {
                            app.getCurrentUser().setAvatarBitmap(response);
                            setupDrawerProfile();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.i("TAG", error.toString());
                        }
                    });
                }

                @Override
                public void onError(Login.LoginError error) {
                    Log.i(TAG, "error logging in");
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                }
            });
        }

    }

    private void setupToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        if (mToolbar != null)
            setSupportActionBar(mToolbar);
    }

    private void setupBaseUI() {
        setupToolbar();
        setupDrawer();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "on create base");
        app = (MyApplication) getApplicationContext();
    }


    private void loginWithToken(Login.LoginCallback loginCallback) {
        mLoginRequest = new Login();
        mPref = getApplicationContext().getSharedPreferences(PREFERENCES_NAME, 0);
        mEditor = mPref.edit();

        String token = mPref.getString("token", null);

        if (token == null) {
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            JsonObjectRequest request = mLoginRequest.loginWithToken(token, loginCallback);
            mRequestQueue.add(request);
        }
    }



    protected void setupDrawerProfile() {
        IProfile profile = mDrawerHeader.getActiveProfile();
        profile.withName(app.getCurrentUser().getName());
        profile.withEmail(app.getCurrentUser().getEmail());
        profile.withIcon(app.getCurrentUser().getAvatarBitmap());
        mDrawerHeader.updateProfile(profile);
    }

    private void setupSelectedDrawerItem(Integer selected) {
        mDrawer.setSelection(selected);
    }


    protected void setupDrawer() {
        Log.i(TAG, "setup drawer");
        mDrawerHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .addProfiles(new ProfileDrawerItem())
                .withHeaderBackground(R.drawable.background)
                .withSelectionListEnabledForSingleProfile(false)
                .build();

        SecondaryDrawerItem itemHome = new SecondaryDrawerItem().withName(getResources().getString(R.string.drawer_home)).withIcon(GoogleMaterial.Icon.gmd_home).withIdentifier(DRAWER_HOME);
        SecondaryDrawerItem itemProfile = new SecondaryDrawerItem().withName(getResources().getString(R.string.drawer_profile)).withIcon(GoogleMaterial.Icon.gmd_person_pin).withIdentifier(DRAWER_MY_PROFILE);
        SecondaryDrawerItem itemNetworks = new SecondaryDrawerItem().withName(getResources().getString(R.string.drawer_networks)).withIcon(GoogleMaterial.Icon.gmd_share).withIdentifier(DRAWER_NETWORKS);
        SecondaryDrawerItem itemPictures = new SecondaryDrawerItem().withName(getResources().getString(R.string.drawer_pictures)).withIcon(GoogleMaterial.Icon.gmd_photo_library).withIdentifier(DRAWER_PICTURES);
        SecondaryDrawerItem itemHelp = new SecondaryDrawerItem().withName(getResources().getString(R.string.drawer_help)).withIcon(GoogleMaterial.Icon.gmd_help).withIdentifier(DRAWER_HELP);
        SecondaryDrawerItem itemLogout = new SecondaryDrawerItem().withName(getResources().getString(R.string.drawer_logout)).withIcon(GoogleMaterial.Icon.gmd_settings_power).withIdentifier(DRAWER_LOGOUT);

        mDrawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(mToolbar)
                .withAccountHeader(mDrawerHeader)
                .withSelectedItemByPosition(2)
                .addDrawerItems(
                        itemHome,
                        itemProfile,
                        itemNetworks,
                        itemPictures,
                        itemHelp,
                        itemLogout
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        Log.i("TAG", String.valueOf(position));
                        if (drawerItem == null) return false;
                        switch (drawerItem.getIdentifier()) {
                            case 1:
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                break;
                            case 2:
                                startActivity(new Intent(getApplicationContext(), MyProfileActivity.class));
                                break;
                            case 3:
                                startActivity(new Intent(getApplicationContext(), NetworksActivity.class));
                                break;
                            case 4:
                                startActivity(new Intent(getApplicationContext(), PicturesActivity.class));
                                break;
                            case 5:
                                startActivity(new Intent(getApplicationContext(), HelpActivity.class));
                                break;
                            case 6:
                                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                break;

                        }
                        return true;
                    }
                })
                .build();
    }

    public interface MyRequestListener {
        void onSuccess(JSONObject response);
        void onError(Error error);
    }

    protected JsonObjectRequest makeRequest(Integer method, String url,  JSONObject params, final MyRequestListener myRequestListener){
        return new JsonObjectRequest(
                method,
                url,
                params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Boolean success;
                        try {
                            success = response.getBoolean("success");
                            if (success) {
                                myRequestListener.onSuccess(response);
                            }
                        } catch (JSONException e) {
                            myRequestListener.onError(new Error(e.toString()));
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                myRequestListener.onError(new Error(error.toString()));
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return super.getHeaders();
            }
        };
    }

}
