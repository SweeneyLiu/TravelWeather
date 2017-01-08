package com.lsw.weather.util;

import android.content.Context;

import com.lsw.weather.R;

import java.util.Calendar;


public class ImageUtils {

    public static int getIconByCode(Context context, String code) {
        return context.getResources().getIdentifier("ic_weather_icon_" + code, "drawable", context.getPackageName());
    }

    public static int getWeatherImage(String weather) {
        if (weather.contains("转")) {
            weather = weather.substring(0, weather.indexOf("转"));
        }
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hour >= 7 && hour < 19) {
            if (weather.contains("晴")) {
                return R.drawable.header_weather_day_sunny;
            }
            if (weather.contains("云") || weather.contains("阴")) {
                return R.drawable.header_weather_day_cloudy;
            }
            if (weather.contains("雨")) {
                return R.drawable.header_weather_day_rain;
            }
            if (weather.contains("雪") || weather.contains("冰雹")) {
                return R.drawable.header_weather_day_snow;
            }
            if (weather.contains("雾") || weather.contains("霾") || weather.contains("沙") || weather.contains("浮尘")) {
                return R.drawable.header_weather_day_fog;
            }
            return R.drawable.header_sunrise;
        } else {
            if (weather.contains("晴")) {
                return R.drawable.header_weather_night_sunny;
            }
            if (weather.contains("云") || weather.contains("阴")) {
                return R.drawable.header_weather_night_cloudy;
            }
            if (weather.contains("雨")) {
                return R.drawable.header_weather_night_rain;
            }
            if (weather.contains("雪") || weather.contains("冰雹")) {
                return R.drawable.header_weather_night_snow;
            }
            if (weather.contains("雾") || weather.contains("霾") || weather.contains("沙") || weather.contains("浮尘")) {
                return R.drawable.header_weather_day_fog;
            }
            return R.drawable.header_sunset;
        }
    }

}
