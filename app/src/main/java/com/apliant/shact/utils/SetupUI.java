package com.apliant.shact.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.apliant.shact.LoginActivity;
import com.apliant.shact.MainActivity;
import com.apliant.shact.MyProfileActivity;
import com.apliant.shact.NetworksActivity;
import com.apliant.shact.R;
import com.apliant.shact.PicturesActivity;
import com.apliant.shact.network.VolleySingleton;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

/**
 * Created by rafa93br on 08/01/2016.
 */
public class SetupUI {
    public static void drawer(final Activity activity, Toolbar toolbar, Integer selected, String name, String email, String imageUrl) {
        final Bitmap[] profileImage = new Bitmap[1];

        // Retrieves an image specified by the URL, displays it in the UI.




        final AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(activity)
                .withHeaderBackground(R.drawable.background)
                .withSelectionListEnabledForSingleProfile(false)
                .addProfiles(
                        new ProfileDrawerItem()
                                .withName(name)
                                .withEmail(email)
                                .withIcon(ContextCompat.getDrawable(activity, R.drawable.profile))
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();

        Log.i("TAG", Boolean.toString(URLUtil.isValidUrl(imageUrl)));
        Log.i("TAG", String.valueOf(imageUrl));
        if (URLUtil.isValidUrl(imageUrl)) {
            ImageRequest request = new ImageRequest(
                    imageUrl,
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap response) {
                            IProfile profile = headerResult.getActiveProfile();
                            profile.withIcon(response);
                            headerResult.updateProfile(profile);
                            Log.i("TAG", "OK!");
                        }
                    }, 0, 0, ImageView.ScaleType.CENTER, Bitmap.Config.RGB_565,
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.i("TAG", error.toString());
                        }
                    }
            );

            RequestQueue requestQueue = VolleySingleton.getInstance().getRequestQueue();
            requestQueue.add(request);
        }


        PrimaryDrawerItem itemHome = new PrimaryDrawerItem().withName(activity.getResources().getString(R.string.drawer_home)).withIcon(GoogleMaterial.Icon.gmd_home);
        SecondaryDrawerItem itemProfile = new SecondaryDrawerItem().withName(activity.getResources().getString(R.string.drawer_profile)).withIcon(GoogleMaterial.Icon.gmd_person_pin);
        SecondaryDrawerItem itemNetworks = new SecondaryDrawerItem().withName(activity.getResources().getString(R.string.drawer_networks)).withIcon(GoogleMaterial.Icon.gmd_share);
        SecondaryDrawerItem itemPictures = new SecondaryDrawerItem().withName(activity.getResources().getString(R.string.drawer_pictures)).withIcon(GoogleMaterial.Icon.gmd_photo_library);
        SecondaryDrawerItem itemHelp = new SecondaryDrawerItem().withName(activity.getResources().getString(R.string.drawer_help)).withIcon(GoogleMaterial.Icon.gmd_help);
        SecondaryDrawerItem itemLogout = new SecondaryDrawerItem().withName(activity.getResources().getString(R.string.drawer_logout)).withIcon(GoogleMaterial.Icon.gmd_settings_power);

        Drawer result = new DrawerBuilder()
                .withActivity(activity)
                .withToolbar(toolbar)
                .withSelectedItemByPosition(selected)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        itemHome,
                        itemProfile,
                        itemNetworks,
                        itemPictures,
                        itemHelp,
                        new DividerDrawerItem(),
                        itemLogout
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        Log.i("TAG", Integer.toString(position));
                        switch (position) {
                            case 1:
                                activity.startActivity(new Intent(activity, MainActivity.class));
                                break;
                            case 2:
                                activity.startActivity(new Intent(activity, MyProfileActivity.class));
                                break;
                            case 3:
                                activity.startActivity(new Intent(activity, NetworksActivity.class));
                                break;
                            case 4:
                                activity.startActivity(new Intent(activity, PicturesActivity.class));
                                break;
                            case 5:
                                activity.startActivity(new Intent(activity, LoginActivity.class));
                                break;
                            case 6:
                                //TODO: LOGOUT
                                Log.i("TAG", "AQUI!");
                                activity.startActivity(new Intent(activity, LoginActivity.class));
                                break;

                        }

                        return true;
                    }
                })
                .build();


    }
}
