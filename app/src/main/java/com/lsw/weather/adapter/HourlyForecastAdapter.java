package com.lsw.weather.adapter;

import android.content.Context;
import android.media.MediaCodec;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lsw.weather.R;
import com.lsw.weather.model.WeatherEntity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;


public class HourlyForecastAdapter extends BaseAdapter {
    private List<WeatherEntity.HeWeatherBean.HourlyForecastBean> mData;

    public HourlyForecastAdapter(List<WeatherEntity.HeWeatherBean.HourlyForecastBean> data) {
        mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.view_holder_hourly_forecast, parent, false);
            holder = new ViewHolder();
            holder.tvTime = (TextView)convertView.findViewById(R.id.tv_time);
            holder.tvTemp = (TextView)convertView.findViewById(R.id.tv_temp);
            holder.tvHum = (TextView)convertView.findViewById(R.id.tv_hum);
            holder.tvWind = (TextView)convertView.findViewById(R.id.tv_wind);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        holder.tvTime.setText(timeFormat(mData.get(position).getDate()));
        holder.tvTemp.setText(context.getString(R.string.temp, mData.get(position).getTmp()));
        holder.tvHum.setText(context.getString(R.string.hourly_forecast_hum, mData.get(position).getHum()));
        holder.tvWind.setText(windFormat(context, mData.get(position).getWind().getSc()));
        return convertView;
    }

   class ViewHolder {
        TextView tvTime;
        TextView tvTemp;
        TextView tvHum;
        TextView tvWind;
    }

    private String timeFormat(String time) {
        SimpleDateFormat fromSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        SimpleDateFormat toSdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        try {
            return toSdf.format(fromSdf.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return toSdf.format(new Date());
    }

    private String windFormat(Context context, String wind) {
        if (wind.contains("风")) {
            return wind;
        } else {
            return context.getString(R.string.hourly_forecast_wind, wind);
        }
    }
}
