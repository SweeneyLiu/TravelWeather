package com.lsw.weather.activity;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lsw.weather.R;
import com.lsw.weather.adapter.DailyForecastAdapter;
import com.lsw.weather.adapter.HourlyForecastAdapter;
import com.lsw.weather.adapter.SuggestionAdapter;
import com.lsw.weather.api.WeatherApi;
import com.lsw.weather.model.WeatherEntity;
import com.lsw.weather.model.WeatherEntity.HeWeatherBean;
import com.lsw.weather.util.HttpUtil;
import com.lsw.weather.util.ImageUtils;
import com.lsw.weather.view.ScrollListView;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.iv_weather_image)
    ImageView ivWeatherImage;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.fab_speech)
    FloatingActionButton fabSpeech;
    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.iv_icon)
    ImageView ivIcon;
    @BindView(R.id.tv_temp)
    TextView tvTemp;
    @BindView(R.id.tv_max_temp)
    TextView tvMaxTemp;
    @BindView(R.id.tv_min_temp)
    TextView tvMinTemp;
    @BindView(R.id.tv_more_info)
    TextView tvMoreInfo;
    @BindView(R.id.ll_weather_container)
    LinearLayout llWeatherContainer;
    @BindView(R.id.nested_scroll_view)
    NestedScrollView nestedScrollView;
    @BindView(R.id.lv_hourly_forecast)
    ScrollListView lvHourlyForecast;
    @BindView(R.id.lv_daily_forecast)
    ScrollListView lvDailyForecast;
    @BindView(R.id.lv_suggestion)
    ScrollListView lvSuggestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);


        loadWeatherData();

        swipeRefreshLayout.setColorSchemeResources(R.color.bg_orange, R.color.bg_blue, R.color.bg_green, R.color.bg_red);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                loadWeatherData();
            }
        });

        fabSpeech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        navView.setNavigationItemSelectedListener(this);
    }

    private void loadWeatherData() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HttpUtil.WEATHER_URL)
                .addConverterFactory(GsonConverterFactory.create())//添加 json 转换器
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())//添加 RxJava 适配器
                .build();

        WeatherApi weatherApi = retrofit.create(WeatherApi.class);

        weatherApi.getWeather("beijing", HttpUtil.HE_WEATHER_KEY)//发起请求
                .subscribeOn(Schedulers.io())//在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())//回到主线程去处理请求注册结果
                .subscribe(new Observer<WeatherEntity>() {
                    @Override
                    public void onCompleted() {
                        Log.d("sweeney---", "onCompleted: ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("sweeney---", "onError: " + e.getMessage());
                    }

                    @Override
                    public void onNext(WeatherEntity entity) {
                        Log.d("sweeney---", "onNext: ");
                        updateView(entity.getHeWeather().get(0));
                    }
                });
    }

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void updateView(HeWeatherBean weather) {
        ivWeatherImage.setImageResource(ImageUtils.getWeatherImage(weather.getNow().getCond().getTxt()));
        ivIcon.setImageResource(ImageUtils.getIconByCode(this, weather.getNow().getCond().getCode()));
        tvTemp.setText(getString(R.string.tempC, weather.getNow().getTmp()));
        tvMaxTemp.setText(getString(R.string.now_max_temp, weather.getDaily_forecast().get(0).getTmp().getMax()));
        tvMinTemp.setText(getString(R.string.now_min_temp, weather.getDaily_forecast().get(0).getTmp().getMin()));
        StringBuilder sb = new StringBuilder();
        sb.append("体感").append(weather.getNow().getFl()).append("°");
        if (weather.getAqi() != null && !TextUtils.isEmpty(weather.getAqi().getCity().getQlty())) {
            sb.append("  ").append(weather.getAqi().getCity().getQlty().contains("污染") ? "" : "空气").append(weather.getAqi().getCity().getQlty());
        }
        sb.append("  ").append(weather.getNow().getWind().getDir()).append(weather.getNow().getWind().getSc()).append(weather.getNow().getWind().getSc().contains("风") ? "" : "级");
        tvMoreInfo.setText(sb.toString());
        lvHourlyForecast.setAdapter(new HourlyForecastAdapter(weather.getHourly_forecast()));
        lvDailyForecast.setAdapter(new DailyForecastAdapter(weather.getDaily_forecast()));
        lvSuggestion.setAdapter(new SuggestionAdapter(weather.getSuggestion()));
    }

}
