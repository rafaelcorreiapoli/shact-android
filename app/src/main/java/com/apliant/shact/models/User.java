package com.apliant.shact.models;

import android.graphics.Bitmap;
import android.util.Log;
import android.webkit.URLUtil;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.apliant.shact.network.VolleySingleton;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

/**
 * Created by rafa93br on 07/01/2016.
 */
public class User {
    private String name;
    private String username;
    private String email;
    private String avatar;
    private String _id;
    private Bitmap avatarBitmap;

    public User(String name, String username, String email, String avatar, String _id) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.avatar = avatar;
        this._id = _id;
    }

    public Bitmap getAvatarBitmap() {
        return avatarBitmap;
    }

    public void setAvatarBitmap(Bitmap avatarBitmap) {
        this.avatarBitmap = avatarBitmap;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public User() {
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void downloadAvatar(Response.Listener responseListener, Response.ErrorListener errorListener) {
        if (URLUtil.isValidUrl(avatar)) {
            ImageRequest request = new ImageRequest(
                    avatar,
                    responseListener,
                    0, 0,
                    ImageView.ScaleType.CENTER,
                    Bitmap.Config.RGB_565,
                    errorListener
            );

            RequestQueue requestQueue = VolleySingleton.getInstance().getRequestQueue();
            requestQueue.add(request);
        }
    }
}
