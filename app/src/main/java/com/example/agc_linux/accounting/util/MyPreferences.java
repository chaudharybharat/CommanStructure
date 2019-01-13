package com.example.agc_linux.accounting.util;

import android.content.Context;
import android.content.SharedPreferences;


public class MyPreferences {

    public static final int GET_STRING = 0;
    public static final int GET_INT = 1;
    public static final int GET_LONG = 2;
    public static final int GET_FLOAT = 3;
    public static final int GET_BOOLEAN = 4;


    public static String prefID = "Account";

    //preferences name
    public static String LOGIN_PREFERENCES = "login_preferences";
    public static Object getPref(Context mContext, String mPrefKey, int type) {
        try {
            SharedPreferences prefs = mContext.getSharedPreferences(prefID, Context.MODE_PRIVATE);
            switch (type) {
                case 0:
                    return prefs.getString(mPrefKey, "");
                case 1:
                    return prefs.getInt(mPrefKey, 0);
                case 2:
                    return prefs.getLong(mPrefKey, 0);
                case 3:
                    return prefs.getFloat(mPrefKey, 0);
                case 4:
                    return prefs.getBoolean(mPrefKey, false);
                default:
                    return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static String getPref(Context mContext, String mPrefKey) {
        try {
            SharedPreferences prefs = mContext.getSharedPreferences(prefID, Context.MODE_PRIVATE);
            return prefs.getString(mPrefKey, "");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    public static String getPrefLanguage(Context mContext, String mPrefKey) {
        try {
            SharedPreferences prefs = mContext.getSharedPreferences(prefID, Context.MODE_PRIVATE);
            return prefs.getString(mPrefKey, "en");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static void setPrefClearAll(Context mContext) {
       try{
           SharedPreferences prefs = mContext.getSharedPreferences(prefID, Context.MODE_PRIVATE);
           prefs.edit().clear().commit();
       }catch (Exception e){
           e.printStackTrace();
       }
    }
    public static void setPrefClear(Context mContext, String mPrefKey, String mPrefValue) {
        try {
            SharedPreferences.Editor editor = mContext.getSharedPreferences(prefID, Context.MODE_PRIVATE).edit();
            editor.remove(mPrefKey);
            editor.apply();
            editor.commit();
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public static void setPref(Context mContext, String mPrefKey, String mPrefValue) {
        try {
            SharedPreferences.Editor editor = mContext.getSharedPreferences(prefID, Context.MODE_PRIVATE).edit();
            if (mPrefValue != null) {
                editor.remove(mPrefKey);
                editor.putString(mPrefKey, mPrefValue);
            } else {
                editor.remove(mPrefKey);
                editor.putString(mPrefKey, "");
            }
            editor.apply();
        } catch (Exception e) {

            e.printStackTrace();
        }
    }


}
