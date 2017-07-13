package com.tonyhu.location;

import android.app.Application;
import android.content.Context;

import com.baidu.mapapi.SDKInitializer;

/**
 * Created by Administrator on 2017/3/23.
 */

public class TonyLocationApplication extends Application {

    private static Context context;


    @Override
    public void onCreate() {
        super.onCreate();
        context = this.getApplicationContext();
        SDKInitializer.initialize(getApplicationContext());
        CrashHandler.getInstance().init();
    }

    public static Context getContext() {
        return context;
    }


}
