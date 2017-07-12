package com.tonyhu.location.activity;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;

import com.qq.e.ads.splash.SplashAD;
import com.qq.e.ads.splash.SplashADListener;
import com.tonyhu.location.R;
import com.tonyhu.location.util.Constants;

public class WelcomeActivity extends BaseActivity {
    private final static int DURATION = 2000;
    private Handler handler;
    private LinearLayout adContainer;

    @Override
    public void onCreate(Bundle savedInstance) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_welcome);
        adContainer = (LinearLayout)findViewById(R.id.adContainer) ;
        getSplashAd();
        startTimer();
    }

    private void startTimer() {
        AlphaAnimation aa = new AlphaAnimation(0.0f,1.0f);
        aa.setDuration(DURATION);
        findViewById(R.id.text1).startAnimation(aa);
        findViewById(R.id.text2).startAnimation(aa);

//        handler = new Handler();
//        try {
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    Intent intent = new Intent(WelcomeActivity.this,MainActivity.class);
//                    startActivity(intent);
//                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
//                    finish();
//                }
//            },DURATION);
//        } catch (Exception e){
//            e.printStackTrace();
//        }
    }

    public  void getSplashAd(){
        //参数 this, container, Constants.APPId, Constants.SplashPosId
        //创建开屏广告，广告拉取成功后会自动展示在container中。Container会首先被清空||测试1101152570  8863364436303842593
        //注意 Constants.APPId和Constants.SplashPosId后期要替换成自己的
        new SplashAD(WelcomeActivity.this, adContainer, Constants.GDT_APPID, Constants.GDT_SPLASH_POS_ID,
                new SplashADListener() {


                    //广告展示时间结束（5s）或者用户点击关闭时调用。
                    @Override
                    public void onADDismissed() {
                        // TODO Auto-generated method stub
                        Intent intent = new Intent(WelcomeActivity.this,
                                MainActivity.class);

                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                        finish();
                    }
                    //广告拉取成功开始展示时调用
                    @Override
                    public void onADPresent() {
                        // TODO Auto-generated method stub
                        Log.d("hjs","splash ad onPresent");
                    }

                    @Override
                    public void onADClicked() {
                    }

                    @Override
                    public void onADTick(long l) {
                    }

                    //广告拉取超时（3s）或者没有广告时调用，errCode参见SplashAd类的常量声明
                    @Override
                    public void onNoAD(int code) {
                        Log.d("hjs","splash ad no ad，errorCode：" + code);
                        handler = new Handler();
                        try {
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(WelcomeActivity.this,MainActivity.class);
                                    startActivity(intent);
                                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                                    finish();
                                }
                            },DURATION / 2);
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                });


    }
}
