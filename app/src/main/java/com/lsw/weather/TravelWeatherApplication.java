package com.lsw.weather;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;

import cn.hugeterry.updatefun.config.UpdateKey;

public class TravelWeatherApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
        UpdateKey.API_TOKEN = "b109dacaa1b9a6adc83f5df86bd39fc3";
        UpdateKey.APP_ID = "com.lsw.weather";
    }

    public static Context getContext(){
        return context;
    }
}
