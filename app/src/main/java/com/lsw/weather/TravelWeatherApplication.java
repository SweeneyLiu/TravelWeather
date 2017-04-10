package com.lsw.weather;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;

import im.fir.sdk.FIR;


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
        FIR.init(this);
    }

    public static Context getContext(){
        return context;
    }
}
