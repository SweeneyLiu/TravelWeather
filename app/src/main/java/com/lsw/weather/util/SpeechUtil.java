package com.lsw.weather.util;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;

/**
 * Created by Administrator on 2017/1/18 0018.
 */

public class SpeechUtil implements SpeechSynthesizerListener {
    protected static final int UI_LOG_TO_VIEW = 0;
    private SpeechSynthesizer speechSynthesizer;
    private Context context;

    public SpeechUtil(Context activity) {
        this.context = activity;
        init();
    }
    /**
     * 初始化合成相关组件
     */
    private void init() {
        speechSynthesizer = SpeechSynthesizer.getInstance();
        speechSynthesizer.setContext(context);
        //此处需要将setApiKey方法的两个参数替换为你在百度开发者中心注册应用所得到的apiKey和secretKey
        speechSynthesizer.setApiKey("LhS9kXyayGYZwrMY4lQ1Sh2F", "db5b9a8f0403a921d1226673dffd0113");
        speechSynthesizer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        //activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        setParams();
    }
    /**
     * 开始文本合成并朗读
     * @param content
     */
    public void speak(final String content) {
        new Thread(new Runnable() {
            @Override
            public void run() {
//                 setParams();
                int ret = speechSynthesizer.speak(content.toString());
                if (ret != 0) {
                    Log.e("inf","开始合成器失败："+ret);
                }
            }
        }).start();
    }

    /**
     * 暂停文本朗读，如果没有调用speak(String)方法或者合成器初始化失败，该方法将无任何效果
     */
    public void pause() {
        speechSynthesizer.pause();
    }
    /**
     * 继续文本朗读，如果没有调用speak(String)方法或者合成器初始化失败，该方法将无任何效果
     */
    public void resume() {
        speechSynthesizer.resume();
    }
    /**
     * 为语音合成器设置相关参数
     */
    private void setParams() {
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "0");//发音人，目前支持女声(0)和男声(1)
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_VOLUME, "9");//音量，取值范围[0, 9]，数值越大，音量越大
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEED, "5");//朗读语速，取值范围[0, 9]，数值越大，语速越快
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_PITCH, "5");//音调，取值范围[0, 9]，数值越大，音量越高
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_AUDIO_ENCODE,
                SpeechSynthesizer.AUDIO_ENCODE_AMR);//音频格式，支持bv/amr/opus/mp3，取值详见随后常量声明
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_AUDIO_RATE,
                SpeechSynthesizer.AUDIO_BITRATE_AMR_15K85);//音频比特率，各音频格式支持的比特率详见随后常量声明
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
    public void onError(String s, SpeechError speechError) {

    }
}
