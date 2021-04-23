package com.example.dashboard.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.dashboard.model.User;

public class Preferences {
    private static Preferences preferences;

    private SharedPreferences sharedPreferences;
    private User currentUser;

    private static final String USER_INFO = "userInfoForFoodBuy";
    private static final String USER_ID = "userId";
    private static final String USER_EMAIL = "userEmail";
    private static final String USER_NAME = "userName";
    private static final String USER_TYPE = "userType";
    private static final String USER_STORE_NAME = "storeName";
    private static final String USER_PHONE = "userPhone";
    private static final String USER_ADDRESS_TEXT = "userAddressText";
    private static final String USER_LONGITUDE = "userLongitude";
    private static final String USER_LATITUDE = "userLatitude";


    private Preferences(Context context) {
        sharedPreferences = context.getSharedPreferences(USER_INFO, Context.MODE_PRIVATE);
    }

    //pass applicationContext here because this is a singleton class
    public static Preferences getPreferences(Context context) {
        if(preferences == null) {
            preferences = new Preferences(context.getApplicationContext());
        }
        return preferences;
    }

    public void saveUser(User user) {
        if(user == null) {
            return;
        }

        currentUser = user;

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(USER_ID, user._id);
        editor.putString(USER_EMAIL, user.email);
        editor.putString(USER_NAME, user.name);
        editor.putString(USER_TYPE, user.userType);
        editor.putString(USER_STORE_NAME, user.storeName);
        editor.putString(USER_PHONE, user.phone);
        if(user.address != null) {
            editor.putString(USER_ADDRESS_TEXT, user.address.addressText);
            editor.putString(USER_LONGITUDE, Double.toString(user.address.longitude));
            editor.putString(USER_LATITUDE, Double.toString(user.address.latitude));
        }

        editor.commit();
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void logoutCurrentUser() {
        currentUser = null;
    }
}
