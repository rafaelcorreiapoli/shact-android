package com.apliant.shact.models;

import android.content.Context;
import android.graphics.Color;

import com.mikepenz.iconics.IconicsDrawable;

/**
 * Created by rafa93br on 08/01/2016.
 */
public class Network {
    String _id;
    String name;
    Integer color;
    String icon;
    String identifier;
    User.Profile profile;

    public User.Profile getProfile() {
        return profile;
    }

    public void setProfile(User.Profile profile) {
        this.profile = profile;
    }

    public Network(String _id, String name, Integer color, String icon, String identifier) {
        this._id = _id;
        this.name = name;
        this.color = color;
        this.icon = icon;
        this.identifier = identifier;
    }


    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public IconicsDrawable getDrawable(Context context, Integer color) {
        return new IconicsDrawable(context, getIcon())
                .sizeDp(150)
                .paddingDp(30)
                .contourWidthDp(3)
                .contourColor(Color.BLACK)
                .color(color);
    }


    public Integer getColor() {
        return color;
    }

    public void setColor(Integer color) {
        this.color = color;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
}
