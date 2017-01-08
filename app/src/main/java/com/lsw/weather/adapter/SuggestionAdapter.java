package com.lsw.weather.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lsw.weather.R;
import com.lsw.weather.model.WeatherEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SuggestionAdapter extends BaseAdapter {
    private List<Integer> mIcon = new ArrayList<>();
    private List<String> mTitle = new ArrayList<>();
    private List<WeatherEntity.Entity> mData = new ArrayList<>();

    public SuggestionAdapter(WeatherEntity.HeWeatherBean.SuggestionBean data) {
        mIcon.add(R.drawable.ic_suggestion_comfort);
        mTitle.add("舒适度");
        mData.add(new WeatherEntity.Entity(data.getAir().getBrf(),data.getAir().getTxt()));
        mIcon.add(R.drawable.ic_suggestion_clothe);
        mTitle.add("穿衣");
        mData.add(new WeatherEntity.Entity(data.getDrsg().getBrf(),data.getDrsg().getTxt()));
        mIcon.add(R.drawable.ic_suggestion_flu);
        mTitle.add("感冒");
        mData.add(new WeatherEntity.Entity(data.getFlu().getBrf(),data.getFlu().getTxt()));
        mIcon.add(R.drawable.ic_suggestion_car);
        mTitle.add("洗车");
        mData.add(new WeatherEntity.Entity(data.getCw().getBrf(),data.getCw().getTxt()));
        mIcon.add(R.drawable.ic_suggestion_sport);
        mTitle.add("运动");
        mData.add(new WeatherEntity.Entity(data.getSport().getBrf(),data.getSport().getTxt()));
        mIcon.add(R.drawable.ic_suggestion_travel);
        mTitle.add("旅游");
        mData.add(new WeatherEntity.Entity(data.getTrav().getBrf(),data.getTrav().getTxt()));
        mIcon.add(R.drawable.ic_suggestion_uv);
        mTitle.add("紫外线");
        mData.add(new WeatherEntity.Entity(data.getUv().getBrf(),data.getUv().getTxt()));
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
            convertView = LayoutInflater.from(context).inflate(R.layout.view_holder_suggestion, parent, false);
            holder = new ViewHolder();
            holder.ivIcon = (ImageView)convertView.findViewById(R.id.iv_icon);
            holder.tvLabel = (TextView)convertView.findViewById(R.id.tv_label);
            holder.tvTitle = (TextView)convertView.findViewById(R.id.tv_title);
            holder.tvDesc = (TextView)convertView.findViewById(R.id.tv_desc);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.ivIcon.setImageResource(mIcon.get(position));
        holder.tvLabel.setText(mTitle.get(position));
        holder.tvTitle.setText(mData.get(position).brf);
        holder.tvDesc.setText(mData.get(position).txt);
        return convertView;
    }

    class ViewHolder {

        ImageView ivIcon;
        TextView tvLabel;
        TextView tvTitle;
        TextView tvDesc;

        public ViewHolder() {
        }
    }
}
