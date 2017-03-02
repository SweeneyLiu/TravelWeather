package com.lsw.weather;

import android.app.Application;

import com.antfortune.freeline.FreelineCore;
import com.squareup.leakcanary.LeakCanary;

public class TravelWeatherApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
        FreelineCore.init(this);
    }
}
