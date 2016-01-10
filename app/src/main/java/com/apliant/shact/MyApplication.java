package com.apliant.shact;

import android.app.Application;
import android.content.Context;

import com.apliant.shact.models.User;

/**
 * Created by rafa93br on 06/01/2016.
 */
public class MyApplication extends Application {
    private static MyApplication sInstance;
    private User currentUser;

    public void setCurrentUser(User user){
        this.currentUser = user;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    @Override
    public void onCreate() {
        sInstance = this;
        super.onCreate();
    }

    public static MyApplication getInstance() {
        return sInstance;
    }

    public static Context getAppContext() {
        return sInstance.getApplicationContext();
    }
}
