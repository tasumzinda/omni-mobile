package com.stewardbank.omnichannel.business.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.MediaType;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.security.MessageDigest;
import java.security.cert.CertificateException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by tasu on 5/4/17.
 */
public class AppUtil {

    public static String LOGGED_IN = "LOGGED_IN";
    public static String USERNAME = "USERNAME";
    public static String PASSWORD = "PASSWORD";
    public static String BASE_URL = "http://192.168.43.234:8084/omnichannel-mobile-rest/rest/mobile/";
    public static String NAME = "NAME";
    private static AppUtil appInstance;
    private static Context mContext;
    private static Gson gson;
    public static String DATE_FORMAT = "yyyy-MM-dd";
    public static MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private AppUtil(Context context) {
        mContext = context;
    }

    public static synchronized AppUtil getInstance(Context context) {
        if (appInstance == null) {
            appInstance = new AppUtil(context);
        }
        return appInstance;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;
    }

    public static void createLongNotification(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public static void createShortNotification(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void createSnackBarShort(View view, String mgs) {
        Snackbar.make(view, mgs, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }

    public static void createSnackBarLong(View view, String mgs) {
        Snackbar.make(view, mgs, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    public static Gson createGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.excludeFieldsWithoutExposeAnnotation().setDateFormat("dd/MM/yyyy").create();
        return gson;
    }

    public static void savePreferences(Context context, String key, Boolean value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static void savePreferences(Context context, String key, String value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static void savePreferences(Context context, String key, Long value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public static boolean isLoggedIn(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(LOGGED_IN, Boolean.FALSE);
    }

    public static String getUsername(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(USERNAME, "USERNAME");
    }

    public static String getPassword(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(PASSWORD, "PASSWORD");
    }

    public static String getBaseUrl(Context context){
        return BASE_URL;
    }

    public static void removePreferences(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        //sharedPreferences.edit().clear().commit();
        sharedPreferences.edit().remove("LOGGED_IN").commit();
    }


}
