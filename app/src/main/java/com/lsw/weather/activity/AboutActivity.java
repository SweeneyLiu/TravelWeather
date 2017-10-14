package com.lsw.weather.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lsw.weather.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AboutActivity extends BaseActivity {

    private static final String TAG = "AboutActivity";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.suggestion)
    TextView suggestion;
    @BindView(R.id.new_version)
    TextView newVersion;
    @BindView(R.id.favourable_comment)
    TextView favourableComment;
    @BindView(R.id.version)
    TextView version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!= null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.about);
        }

        version.setText("V "+getVersionName());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.suggestion, R.id.new_version, R.id.favourable_comment})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.suggestion:
                Intent intent = new Intent(AboutActivity.this,ContactActivity.class);
                startActivity(intent);
                break;
            case R.id.new_version:
                break;
            case R.id.favourable_comment:
                openApplicationMarket();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * 通过包名 在应用商店打开应用
     */
    private void openApplicationMarket() {
        try {
            String str = "market://details?id=" + getPackageName();
            Intent localIntent = new Intent(Intent.ACTION_VIEW);
            localIntent.setData(Uri.parse(str));
            startActivity(localIntent);
        } catch (Exception e) {
            // 打开应用商店失败 可能是没有手机没有安装应用市场
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "打开应用商店失败", Toast.LENGTH_SHORT).show();
            // 调用系统浏览器进入商城
            String url = "http://app.mi.com/detail/163525?ref=search";
            openLinkBySystem(url);
        }
    }

    /**
     * 调用系统浏览器打开网页
     *
     * @param url 地址
     */
    private void openLinkBySystem(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    private String getVersionName() {
        // 获取packagemanager的实例
        PackageManager packageManager = getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = null;
        String version = "";
        try {
            packInfo = packageManager.getPackageInfo(getPackageName(),0);
            version = packInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

}
