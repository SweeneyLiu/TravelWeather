package com.lsw.weather;

import android.app.Application;
import android.content.Context;

import com.antfortune.freeline.FreelineCore;
import com.squareup.leakcanary.LeakCanary;

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
        FreelineCore.init(this);
    }

    public static Context getContext(){
        return context;
    }
}
