package com.apliant.shact;

import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.apliant.shact.models.User;
import com.apliant.shact.network.VolleySingleton;
import com.apliant.shact.utils.SetupUI;
import com.apliant.shact.utils.UrlEndpoints;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.inject.Inject;
import com.soundcloud.android.crop.Crop;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.filepicker.Filepicker;
import io.filepicker.FilepickerCallback;
import io.filepicker.models.FPFile;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;


public class MyProfileActivity extends BaseActivity implements View.OnClickListener {
    ImageView profileAvatar;
    TextView profileName;
    Bitmap auxBitmap;

    Toolbar toolbar;
    private static final Integer PICK_IMAGE_FROM_GALLERY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        getSupportActionBar().setTitle("Meu Perfil");
        mDrawer.setSelection(DRAWER_MY_PROFILE, false);

        profileAvatar = (ImageView) findViewById(R.id.profileAvatar);
        profileName = (TextView) findViewById(R.id.profileName);

        if(app.getCurrentUser() != null) {
            profileName.setText(app.getCurrentUser().getName());
            profileAvatar.setImageBitmap(app.getCurrentUser().getAvatarBitmap());
        }

        profileAvatar.setOnClickListener(this);
        profileName.setOnClickListener(this);
        Filepicker.setKey("ArPkiQxiLTSSM66CNiGUYz");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profileAvatar:
                EasyImage.openChooser(this, "Selecione uma foto!", true);
                break;
            case R.id.profileName:
                popupSetName();
                break;
        }
    }

    private void popupSetName() {
        final EditText textInput = new EditText(this);

        new AlertDialog.Builder(this)
                .setTitle("Qual seu nome?")
                .setView(textInput)
                .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newName = textInput.getText().toString();
                        app.getCurrentUser().setName(newName);
                        profileName.setText(newName);
                        setupDrawerProfile();
                        mRequestQueue.add(updateUserProfile());
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source) {
                //Some error handling
            }

            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source) {
                //Handle the image
                onPhotoReturned(Uri.fromFile(imageFile));
            }
        });

        if (requestCode == Crop.REQUEST_CROP && resultCode == RESULT_OK) {
            onCroppedReturned(Crop.getOutput(data));
        }

    }

    private JsonObjectRequest updateUserProfile() {
        final String url = UrlEndpoints.BASE_URL + UrlEndpoints.USER + "/" + app.getCurrentUser().get_id();
        Gson gson = new Gson();
        JSONObject userJson = new JSONObject();
        try {
            userJson = new JSONObject(gson.toJson(app.getCurrentUser()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, userJson, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Boolean success;
                Log.i("TAG", response.toString());
                try {
                    success = response.getBoolean("success");
                    if (success) {
                        Log.i(TAG, "success!");
                    } else {
                        Log.i(TAG, "error!");
                    }
                } catch (JSONException e) {
                    Log.i(TAG, "json error");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "request error");
                error.printStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return super.getHeaders();
            }
        };

        return request;
    }

    private void onCroppedReturned(Uri uri)  {
        profileAvatar.setImageURI(uri);
        try {
            auxBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        app.getCurrentUser().setAvatarBitmap(auxBitmap);
        setupDrawerProfile();

        Filepicker.uploadLocalFile(Uri.parse(uri.toString()), this, new FilepickerCallback() {
            @Override
            public void onFileUploadSuccess(FPFile fpFile) {
                // Do something on success
                Log.i(TAG, "Uploaded " + fpFile.getUrl());
                app.getCurrentUser().setAvatar(fpFile.getUrl());
                mRequestQueue.add(updateUserProfile());
            }

            @Override
            public void onFileUploadError(Throwable error) {
                // Do something on error
                Log.i(TAG, "Error" + error.toString());
            }

            @Override
            public void onFileUploadProgress(Uri uri, float progress) {
                // Do something on progress
                Log.i(TAG, "progress: " + String.valueOf(progress));
            }
        });
    }

    private void onPhotoReturned(Uri source) {
        Log.i(TAG, "on photo returned " + source.toString());
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped.bmp"));
        Crop.of(source, destination).asSquare().start(this);
    }

}
