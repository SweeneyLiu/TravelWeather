package com.lsw.weather.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
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

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.baidu.tts.auth.AuthInfo;
import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;
import com.lsw.weather.R;
import com.lsw.weather.adapter.DailyForecastAdapter;
import com.lsw.weather.adapter.HourlyForecastAdapter;
import com.lsw.weather.adapter.SuggestionAdapter;
import com.lsw.weather.api.WeatherApi;
import com.lsw.weather.model.WeatherEntity;
import com.lsw.weather.model.WeatherEntity.HeWeatherBean;
import com.lsw.weather.util.HttpUtil;
import com.lsw.weather.util.ImageUtils;
import com.lsw.weather.util.SnackbarUtils;
import com.lsw.weather.view.ScrollListView;
import com.yanzhenjie.permission.AndPermission;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SpeechSynthesizerListener {

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
    @BindView(R.id.tv_today_weather)
    TextView tvTodayWeather;

    private String cityName = "";
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;
    private HeWeatherBean mHeWeatherBean;

    private SpeechSynthesizer mSpeechSynthesizer;
    private SpeechSynthesizerListener mSpeechSynthesizerListener;
    private static final String APP_ID = "9204443";
    private static final String API_KEY = "LhS9kXyayGYZwrMY4lQ1Sh2F";
    private static final String SECRET_KEY = "db5b9a8f0403a921d1226673dffd0113";


    private String mSampleDirPath;
    private static final String SAMPLE_DIR_NAME = "baiduTTS";
    private static final String SPEECH_FEMALE_MODEL_NAME = "bd_etts_speech_female.dat";
    private static final String SPEECH_MALE_MODEL_NAME = "bd_etts_speech_male.dat";
    private static final String TEXT_MODEL_NAME = "bd_etts_text.dat";
    private static final String LICENSE_FILE_NAME = "temp_license";
    private static final String ENGLISH_SPEECH_FEMALE_MODEL_NAME = "bd_etts_speech_female_en.dat";
    private static final String ENGLISH_SPEECH_MALE_MODEL_NAME = "bd_etts_speech_male_en.dat";
    private static final String ENGLISH_TEXT_MODEL_NAME = "bd_etts_text_en.dat";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);


        AndPermission.with(this)
                .requestCode(100)
                .permission(Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE)
                .send();

//        initialEnv();
//        initialTts();
        voiceAnimation(fabSpeech,false);

        onLocationCity();
        swipeRefreshLayout.setRefreshing(true);
        swipeRefreshLayout.setColorSchemeResources(R.color.bg_orange, R.color.bg_blue, R.color.bg_green, R.color.bg_red);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                loadWeatherData(cityName);
            }
        });

        fabSpeech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                if (mHeWeatherBean != null) {
                    mSpeechSynthesizer = SpeechSynthesizer.getInstance();
                    mSpeechSynthesizer.setContext(MainActivity.this);
//                    mSpeechSynthesizer.setAppId(APP_ID);
                    mSpeechSynthesizer.setApiKey(API_KEY, SECRET_KEY);
                    mSpeechSynthesizer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    String text = voiceWeather(MainActivity.this, mHeWeatherBean);
                    mSpeechSynthesizer.speak(text);

                }

            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        navView.setNavigationItemSelectedListener(this);
    }

    /**
     * 网络加载天气数据
     * @param city
     */
    private void loadWeatherData(String city) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HttpUtil.WEATHER_URL)
                .addConverterFactory(GsonConverterFactory.create())//添加 json 转换器
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())//添加 RxJava 适配器
                .build();

        WeatherApi weatherApi = retrofit.create(WeatherApi.class);
        weatherApi.getWeather(city, HttpUtil.HE_WEATHER_KEY)//发起请求
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
                        swipeRefreshLayout.setRefreshing(false);
                        mHeWeatherBean = entity.getHeWeather().get(0);
                        updateView(mHeWeatherBean);
                        SnackbarUtils.Short(drawerLayout, "已更新至最新天气").show();
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

/*        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }*/

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * 更新天气
     * @param weather
     */
    private void updateView(HeWeatherBean weather) {
        ivWeatherImage.setImageResource(ImageUtils.getWeatherImage(weather.getNow().getCond().getTxt()));
        ivIcon.setImageResource(ImageUtils.getIconByCode(this, weather.getNow().getCond().getCode()));
        tvTodayWeather.setText(weather.getNow().getCond().getTxt());
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


    /**
     * 定位城市
     */
    private void onLocationCity() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(new AMapLocationListener() {

            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation != null) {
                    if (aMapLocation.getErrorCode() == 0) {
                        //可在其中解析amapLocation获取相应内容。
                        cityName = getLocationCityName(aMapLocation.getProvince(), aMapLocation.getCity(), aMapLocation.getDistrict());
                        collapsingToolbar.setTitle(cityName);
                        loadWeatherData(cityName);
                        Log.d("sweeney---", "onLocationChanged: city = " + cityName);
                    } else {
                        //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                        Log.e("AmapError", "location Error, ErrCode:"
                                + aMapLocation.getErrorCode() + ", errInfo:"
                                + aMapLocation.getErrorInfo());
                    }
                }
            }
        });
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
        //获取一次定位结果：
        //该方法默认为false。
        mLocationOption.setOnceLocation(true);
        //获取最近3s内精度最高的一次定位结果：
        //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
        mLocationOption.setOnceLocationLatest(true);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    /**
     * 获取显示天气的地区
     * @param province
     * @param city
     * @param district
     * @return
     */
    private String getLocationCityName(String province, String city, String district) {

        //去掉district的"区"或"县"
        if (district != null) {

            String districtEnd = district.substring((district.length() - 1), district.length());//去最后的字符

            if (districtEnd.equals("区")) {

                return district.substring(0, (district.length() - 1));

            } else if (districtEnd.equals("县")) {

                return district.substring(0, (district.length() - 1));

            } else {
                return district;
            }
        } else if (city != null) {//去掉city的"市"

            String cityEnd = city.substring((city.length() - 1), city.length());
            if (cityEnd.equals("市")) {

                return city.substring(0, (city.length() - 1));

            } else {
                return city;
            }
        } else if (province != null) {//去掉province的"省"或"市"

            String provinceEnd = province.substring((province.length() - 1), province.length());
            if (provinceEnd.equals("省")) {

                return province.substring(0, (province.length() - 1));

            } else if (provinceEnd.equals("市")) {

                return province.substring(0, (province.length() - 1));

            } else {
                return province;
            }
        } else {
            return "北京";
        }

    }



    private void initialEnv() {
        if (mSampleDirPath == null) {
            String sdcardPath = Environment.getExternalStorageDirectory().toString();
            mSampleDirPath = sdcardPath + "/" + SAMPLE_DIR_NAME;
        }
        makeDir(mSampleDirPath);
        copyFromAssetsToSdcard(false, SPEECH_FEMALE_MODEL_NAME, mSampleDirPath + "/" + SPEECH_FEMALE_MODEL_NAME);
        copyFromAssetsToSdcard(false, SPEECH_MALE_MODEL_NAME, mSampleDirPath + "/" + SPEECH_MALE_MODEL_NAME);
        copyFromAssetsToSdcard(false, TEXT_MODEL_NAME, mSampleDirPath + "/" + TEXT_MODEL_NAME);
        copyFromAssetsToSdcard(false, LICENSE_FILE_NAME, mSampleDirPath + "/" + LICENSE_FILE_NAME);
        copyFromAssetsToSdcard(false, "english/" + ENGLISH_SPEECH_FEMALE_MODEL_NAME, mSampleDirPath + "/"
                + ENGLISH_SPEECH_FEMALE_MODEL_NAME);
        copyFromAssetsToSdcard(false, "english/" + ENGLISH_SPEECH_MALE_MODEL_NAME, mSampleDirPath + "/"
                + ENGLISH_SPEECH_MALE_MODEL_NAME);
        copyFromAssetsToSdcard(false, "english/" + ENGLISH_TEXT_MODEL_NAME, mSampleDirPath + "/"
                + ENGLISH_TEXT_MODEL_NAME);
    }

    private void makeDir(String dirPath) {
        File file = new File(dirPath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    /**
     * 将sample工程需要的资源文件拷贝到SD卡中使用（授权文件为临时授权文件，请注册正式授权）
     *
     * @param isCover 是否覆盖已存在的目标文件
     * @param source
     * @param dest
     */
    private void copyFromAssetsToSdcard(boolean isCover, String source, String dest) {
        File file = new File(dest);
        if (isCover || (!isCover && !file.exists())) {
            InputStream is = null;
            FileOutputStream fos = null;
            try {
                is = getResources().getAssets().open(source);
                String path = dest;
                fos = new FileOutputStream(path);
                byte[] buffer = new byte[1024];
                int size = 0;
                while ((size = is.read(buffer, 0, 1024)) >= 0) {
                    fos.write(buffer, 0, size);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void initialTts() {
        this.mSpeechSynthesizer = SpeechSynthesizer.getInstance();
        this.mSpeechSynthesizer.setContext(this);
        this.mSpeechSynthesizer.setSpeechSynthesizerListener(this);
        // 文本模型文件路径 (离线引擎使用)
        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, mSampleDirPath + "/"
                + TEXT_MODEL_NAME);
        // 声学模型文件路径 (离线引擎使用)
        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, mSampleDirPath + "/"
                + SPEECH_FEMALE_MODEL_NAME);
        // 本地授权文件路径,如未设置将使用默认路径.设置临时授权文件路径，LICENCE_FILE_NAME请替换成临时授权文件的实际路径，仅在使用临时license文件时需要进行设置，如果在[应用管理]中开通了正式离线授权，不需要设置该参数，建议将该行代码删除（离线引擎）
        // 如果合成结果出现临时授权文件将要到期的提示，说明使用了临时授权文件，请删除临时授权即可。
//        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_LICENCE_FILE, mSampleDirPath + "/" + LICENSE_FILE_NAME);
        // 请替换为语音开发者平台上注册应用得到的App ID (离线授权)
        this.mSpeechSynthesizer.setAppId("9204443");/*这里只是为了让Demo运行使用的APPID,请替换成自己的id。*/
        // 请替换为语音开发者平台注册应用得到的apikey和secretkey (在线授权)
        this.mSpeechSynthesizer.setApiKey("LhS9kXyayGYZwrMY4lQ1Sh2F", "db5b9a8f0403a921d1226673dffd0113");/*这里只是为了让Demo正常运行使用APIKey,请替换成自己的APIKey*/
        // 发音人（在线引擎），可用参数为0,1,2,3。。。（服务器端会动态增加，各值含义参考文档，以文档说明为准。0--普通女声，1--普通男声，2--特别男声，3--情感男声。。。）
        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "0");
        // 设置Mix模式的合成策略
        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_DEFAULT);
        // 授权检测接口(只是通过AuthInfo进行检验授权是否成功。)
        // AuthInfo接口用于测试开发者是否成功申请了在线或者离线授权，如果测试授权成功了，可以删除AuthInfo部分的代码（该接口首次验证时比较耗时），不会影响正常使用（合成使用时SDK内部会自动验证授权）
        AuthInfo authInfo = this.mSpeechSynthesizer.auth(TtsMode.MIX);


        // 初始化tts
        mSpeechSynthesizer.initTts(TtsMode.MIX);
        // 加载离线英文资源（提供离线英文合成功能）
        /*int result = mSpeechSynthesizer.loadEnglishModel(mSampleDirPath + "/" + ENGLISH_TEXT_MODEL_NAME, mSampleDirPath
                        + "/" + ENGLISH_SPEECH_FEMALE_MODEL_NAME);
        Log.i("initialTts---","loadEnglishModel result=" + result);*/

    }

    /**
     * 语音动画
     * @param fab
     * @param start
     */
    public static void voiceAnimation(FloatingActionButton fab, boolean start) {
        AnimationDrawable animation = (AnimationDrawable) fab.getDrawable();
        if (start) {
            animation.start();
        } else {
            animation.stop();
            animation.selectDrawable(animation.getNumberOfFrames() - 1);
        }
    }

    /**
     * 语音播报内容
     * @param context
     * @param weather
     * @return
     */
    public static String voiceWeather(Context context, HeWeatherBean weather) {
        StringBuilder sb = new StringBuilder();
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hour >= 7 && hour < 12) {
            sb.append("上午好");
        } else if (hour < 19) {
            sb.append("下午好");
        } else {
            sb.append("晚上好");
        }
        sb.append("，");
        sb.append(context.getString(R.string.app_name))
                .append("为您播报")
                .append("，");
        sb.append("今天白天到夜间")
                .append(weather.getDaily_forecast().get(0).getCond().getTxt_d())
                .append("转")
                .append(weather.getDaily_forecast().get(0).getCond().getTxt_n())
                .append("，");
        sb.append("温度")
                .append(weather.getDaily_forecast().get(0).getTmp().getMin())
                .append("~")
                .append(weather.getDaily_forecast().get(0).getTmp().getMax())
                .append("℃")
                .append("，");
        sb.append(weather.getDaily_forecast().get(0).getWind().getDir())
                .append(weather.getDaily_forecast().get(0).getWind().getSc())
                .append(weather.getDaily_forecast().get(0).getWind().getSc().contains("风") ? "" : "级")
                .append("。");
        return sb.toString();
    }

    @Override
    public void onSynthesizeStart(String s) {

    }

    @Override
    public void onSynthesizeDataArrived(String s, byte[] bytes, int i) {

    }

    @Override
    public void onSynthesizeFinish(String s) {

    }

    @Override
    public void onSpeechStart(String s) {

    }

    @Override
    public void onSpeechProgressChanged(String s, int i) {

    }

    @Override
    public void onSpeechFinish(String s) {

    }

    @Override
    public void onError(String s, SpeechError error) {

    }
}
