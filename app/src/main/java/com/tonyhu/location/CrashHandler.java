package com.tonyhu.location;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.util.Log;

import com.tonyhu.location.activity.MainActivity;


public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    private static CrashHandler mInstance;
    private static final String TAG = "CrashHandler";

    /**
     * CrashHandler默认构造函数
     *
     */
    private CrashHandler() {
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    public static CrashHandler getInstance() {
        if (mInstance == null) {
            mInstance = new CrashHandler();
        }
        return mInstance;
    }

    public void init() {
        if (mDefaultHandler == null)
            mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
//        handlerException(thread, ex);

        if (mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, ex);
        }else {
            Intent intent = new Intent(TonyLocationApplication.getContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent restartIntent = PendingIntent.getActivity(
                    TonyLocationApplication.getContext(), 0, intent,
                    PendingIntent.FLAG_ONE_SHOT);
            //退出程序
            AlarmManager mgr = (AlarmManager) TonyLocationApplication.getContext().getSystemService(Context.ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000,
                    restartIntent); // 1秒钟后重启应用
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
            System.gc();
        }
    }


    private void handlerException(Thread thread, final Throwable ex) {
        if (ex == null) {
            Log.w(TAG, " throwable is null");
            return;
        }

        final StackTraceElement[] stack = ex.getStackTrace();
        final String message = ex.getMessage();
        final String className = ex.getClass().getName();
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();

                Looper.loop();
            }

        }.start();
    }


}
