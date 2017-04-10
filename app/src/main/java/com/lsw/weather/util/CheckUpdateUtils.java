package com.lsw.weather.util;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.lsw.weather.BuildConfig;
import com.lsw.weather.TravelWeatherApplication;
import com.lsw.weather.activity.AboutActivity;
import com.lsw.weather.model.UpdateEntity;

import java.text.DecimalFormat;

import im.fir.sdk.FIR;
import im.fir.sdk.VersionCheckCallback;


public class CheckUpdateUtils {

    public static long sDownloadId = 0;

    public static void checkUpdate(final Activity activity) {
        FIR.checkForUpdateInFIR(HttpUtil.FIR_TOKEN, new VersionCheckCallback() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(String versionJson) {
                Log.i("fir", "check from fir.im success! " + "\n" + versionJson);
                if (activity.isFinishing()) {
                    return;
                }
                Gson gson = new Gson();
                UpdateEntity update;
                try {
                    update = gson.fromJson(versionJson, UpdateEntity.class);
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                    return;
                }
                int version = Integer.valueOf(update.getVersion());
                if (version > BuildConfig.VERSION_CODE) {
                    updateDialog(activity, update);
                } else {
                    if (activity instanceof AboutActivity) {
                        Toast.makeText(activity, "已是最新版本", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFail(Exception exception) {
                Log.i("fir", "check fir.im fail! " + "\n" + exception.getMessage());
            }

            @Override
            public void onFinish() {
            }
        });
    }

    private static void updateDialog(final Activity activity, final UpdateEntity update) {
        String message = String.format("v %1$s(%2$sMB)\n\n%3$s", update.getVersionShort(), b2mb(update.getBinary().getFsize()), update.getChangelog());
        new AlertDialog.Builder(activity)
                .setTitle("发现新版本")
                .setMessage(message)
                .setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        download(activity, update);
                    }
                })
                .setNegativeButton("稍后提醒", null)
                .show();
    }

    private static void download(Activity activity, UpdateEntity update) {
        DownloadManager downloadManager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(update.getInstall_url());
        DownloadManager.Request request = new DownloadManager.Request(uri);
        String fileName = String.format("TravelWeather_%s.apk", update.getVersionShort());
        request.setDestinationInExternalPublicDir("Download", fileName);
        request.setMimeType(MimeTypeMap.getFileExtensionFromUrl(update.getInstall_url()));
        request.allowScanningByMediaScanner();
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setAllowedOverRoaming(false);// 不允许漫游
        sDownloadId = downloadManager.enqueue(request);
        Toast.makeText(activity, "正在后台下载", Toast.LENGTH_SHORT).show();
    }

    private static float b2mb(int b) {
        DecimalFormat decimalFormat = new DecimalFormat(".00");
        String MB = decimalFormat.format((float) b / 1024 / 1024);
        return Float.valueOf(MB);
    }
}
