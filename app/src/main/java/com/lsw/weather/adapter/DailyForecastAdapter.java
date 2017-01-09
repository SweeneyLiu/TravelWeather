package com.lsw.weather.adapter;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lsw.weather.R;
import com.lsw.weather.model.WeatherEntity;
import com.lsw.weather.util.ImageUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;



public class DailyForecastAdapter extends BaseAdapter {
    private List<WeatherEntity.HeWeatherBean.DailyForecastBean> mData;

    public DailyForecastAdapter(List<WeatherEntity.HeWeatherBean.DailyForecastBean> data) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.view_holder_dialy_forecast, parent, false);
            holder = new ViewHolder();
            holder.ivIcon = (ImageView)convertView.findViewById(R.id.iv_icon);
            holder.tvDate = (TextView)convertView.findViewById(R.id.tv_date);
            holder.tvDetail = (TextView)convertView.findViewById(R.id.tv_detail);
            holder.tvTemp = (TextView)convertView.findViewById(R.id.tv_temp);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.ivIcon.setImageResource(ImageUtils.getIconByCode(context, mData.get(position).getCond().getCode_d()));
        holder.tvDate.setText(dateFormat(mData.get(position).getDate()));
        holder.tvTemp.setText(context.getString(R.string.daily_forecast_temp, mData.get(position).getTmp().getMin(), mData.get(position).getTmp().getMax()));
        StringBuilder sb = new StringBuilder();
        sb.append(mData.get(position).getCond().getTxt_d());
        if (!mData.get(position).getCond().getTxt_d().equals(mData.get(position).getCond().getTxt_n())) {
            sb.append("转").append(mData.get(position).getCond().getTxt_n());
        }
        sb.append("，")
                .append(mData.get(position).getWind().getDir())
                .append(mData.get(position).getWind().getSc())
                .append(mData.get(position).getWind().getSc().contains("风") ? "" : "级")
                .append("，")
                .append("湿度")
                .append(mData.get(position).getHum())
                .append("%，")
                .append("降水几率")
                .append(mData.get(position).getPop())
                .append("%")
                .append("，")
                .append("日出")
                .append(mData.get(position).getAstro().getSr())
                .append("，")
                .append("日落")
                .append(mData.get(position).getAstro().getSs())
                .append("。");
        holder.tvDetail.setText(sb.toString());
        return convertView;
    }

    class ViewHolder {
        ImageView ivIcon;
        TextView tvDate;
        TextView tvTemp;
        TextView tvDetail;

        ViewHolder() {
        }
    }

    private String dateFormat(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date today = sdf.parse(sdf.format(new Date()));
            Date source = sdf.parse(date);
            if (today.equals(source)) {
                return "今天";
            } else if (source.getTime() - today.getTime() == DateUtils.DAY_IN_MILLIS) {
                return "明天";
            } else {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(source);
                return weekdayFormat(calendar.get(Calendar.DAY_OF_WEEK));
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return date;
        }
    }

    private String weekdayFormat(int weekday) {
        switch (weekday) {
            case 1:
                return "星期日";
            case 2:
                return "星期一";
            case 3:
                return "星期二";
            case 4:
                return "星期三";
            case 5:
                return "星期四";
            case 6:
                return "星期五";
            case 7:
                return "星期六";
            default:
                return "";
        }
    }
}
