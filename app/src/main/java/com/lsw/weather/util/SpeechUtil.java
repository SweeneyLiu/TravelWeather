package com.lsw.weather.util;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

import com.baidu.tts.auth.AuthInfo;
import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;

/**
 * Created by Administrator on 2017/1/18 0018.
 */

public class SpeechUtil implements SpeechSynthesizerListener {

    private static final String APP_ID = "9204443";
    private static final String API_KEY = "LhS9kXyayGYZwrMY4lQ1Sh2F";
    private static final String SECRET_KEY = "db5b9a8f0403a921d1226673dffd0113";
    private Context mContext;
    private SpeechSynthesizer mSpeechSynthesizer;
    private static final String TAG = "SpeechUtil";

    public SpeechUtil(Context context) {
        mContext = context;
        initialTts();
    }

    /**
     * 初始化tts
     */
    private void initialTts() {
        // 获取tts实例
        mSpeechSynthesizer = SpeechSynthesizer.getInstance();
        // 设置app上下文（必需参数）
        mSpeechSynthesizer.setContext(mContext);
        // 设置tts监听器
        mSpeechSynthesizer.setSpeechSynthesizerListener(this);
        // 请替换为语音开发者平台注册应用得到的apikey和secretkey (在线授权)
        mSpeechSynthesizer.setApiKey(API_KEY, SECRET_KEY);
        // 初始化tts各参数
        initTTSParam(this.mSpeechSynthesizer);
        // 授权检测接口(可以不使用，只是验证授权是否成功)
        AuthInfo authInfo = this.mSpeechSynthesizer.auth(TtsMode.MIX);
        if (authInfo.isSuccess()) {
            Log.i(TAG,"auth success");
        } else {
            String errorMsg = authInfo.getTtsError().getDetailMessage();
            Log.i(TAG,"auth failed errorMsg=" + errorMsg);
        }
        /**
         * 初始化 tts引擎，可以指定使用online在线，或者 mix离在线混合引擎 .
         * mix离在线混合引擎会在online在线不能用的情况下自动使在线offline离线引擎 具体参数清查看文档
         */
        mSpeechSynthesizer.initTts(TtsMode.MIX);
    }

    /**
     * 初始化tts参数
     * @param speechSynthesizer
     */
    private void initTTSParam(SpeechSynthesizer speechSynthesizer) {
        // 发音人（在线引擎），可用参数为0,1,2,3。。。（服务器端会动态增加，各值含义参考文档，以文档说明为准。0--普通女声，1--普通男声，2--特别男声，3--情感男声。。。）默认0
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "0");
        // 调整音量 ,范围 [0-9],默认为5
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_VOLUME, "5");
        // 调整语速 ,范围 [0-9],默认为5
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEED, "5");
        // 调整语调,范围 [0-9],默认为5
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_PITCH, "5");
        // 设置Mix模式的合成策略，默认MIX_MODE_DEFAULT, 其它参数请参考文档
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_HIGH_SPEED_NETWORK);
        // 设置音频格式,默认AUDIO_ENCODE_AMR
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_AUDIO_ENCODE, SpeechSynthesizer.AUDIO_ENCODE_AMR);
        // 设置比特率,默认AUDIO_BITRATE_AMR_15K
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_AUDIO_RATE, SpeechSynthesizer.AUDIO_BITRATE_AMR_12K65);
    }

    /**
     * 文本合成并朗读
     * @param content
     */
    public void speak(final String content) {
        int ret = mSpeechSynthesizer.speak(content);
        if (ret != 0) {
            Log.e(TAG,"文本合成失败："+ret);
        }
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
