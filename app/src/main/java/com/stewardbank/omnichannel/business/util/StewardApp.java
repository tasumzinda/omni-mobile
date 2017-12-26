package com.stewardbank.omnichannel.business.util;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.activeandroid.app.Application;

/**
 * @uthor Tasu Muzinda
 */
public class StewardApp extends Application{

    @Override
    public void onCreate(){
        super.onCreate();
        Configuration configuration = new Configuration.Builder(this).create();
        ActiveAndroid.initialize(configuration);
    }

    @Override
    public void onTerminate(){
        super.onTerminate();
        ActiveAndroid.dispose();
    }
}
