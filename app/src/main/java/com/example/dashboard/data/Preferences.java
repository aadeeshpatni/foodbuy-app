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
    private static final String USER_ADDRESS_STREET = "addressStreet";
    private static final String USER_ADDRESS_PINCODE = "addressPincode";
    private static final String USER_ADDRESS_CITY = "addressCity";
    private static final String USER_ADDRESS_STATE = "addressState";

    private Preferences(Context context) {
        sharedPreferences = context.getSharedPreferences(USER_INFO, Context.MODE_PRIVATE);
    }

    //pass applicationContext here because this is a singleton class
    public static Preferences getPreferences(Context context) {
        if(preferences == null) {
            preferences = new Preferences(context);
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
            editor.putString(USER_ADDRESS_STREET, user.address.street);
            editor.putString(USER_ADDRESS_PINCODE, user.address.pincode);
            editor.putString(USER_ADDRESS_CITY, user.address.city);
            editor.putString(USER_ADDRESS_STATE, user.address.state);
        }

        editor.commit();
    }

    public User getCurrentUser() {
        return currentUser;
    }
}
