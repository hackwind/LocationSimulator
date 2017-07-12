package com.tonyhu.location.util;

import java.lang.reflect.Field;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;

import com.tonyhu.location.TonyApplication;

public class ScreenUtil {

    /**
     * convert dip value to px value.
     * @param dpValue
     *            dip value.
     * @return px value.
     */
    public static int dip2px(float dpValue) {
        final float scale = TonyApplication.getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * convert px value to dip value.
     * @param pxValue
     *            px value.
     * @return dip value.
     */
    public static int px2dip(float pxValue) {
        final float scale = TonyApplication.getContext().getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * convert sp value to px value.
     * @param spValue
     *            sp value.
     * @return px value.
     */
    public static int sp2px(float spValue) {
        final float scale = TonyApplication.getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * scale + 0.5f);
    }

    /**
     * convert px value to sp value.
     * @param pxValue
     *            px value.
     * @return sp value.
     */
    public static int px2sp(float pxValue) {
        final float scale = TonyApplication.getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * get screen width pixels
     * 
     * @return
     */

    public static int getScreenWidth() {
        return TonyApplication.getContext().getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * get screen height pixels
     * 
     * @return
     */

    public static int getScreenHeight() {
        return TonyApplication.getContext().getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * get screen density dpi.
     * 
     * @return
     */
    public static int getScreenDpi() {
        return TonyApplication.getContext().getResources().getDisplayMetrics().densityDpi;
    }

    @SuppressLint("NewApi")
    public static void hideSystemUI(View view) {
        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @SuppressLint("NewApi")
    public static void showSystemUI(View view) {
        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    /**
     * 获取状态栏高度
     * 
     * @return penglei
     */
    public static int getBarHeight() {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, sbar = px2dip(25);// 默认为25dp

        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            sbar = TonyApplication.getContext().getResources().getDimensionPixelSize(x);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return sbar;
    }

    /**
     * 通过反射获取状态栏的高度
     * 
     * @return
     */
    public static int getStatusBarHeight() {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, sbar = 0;
        try {

            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            sbar = TonyApplication.getContext().getResources().getDimensionPixelSize(x);

        } catch (Exception e1) {
            e1.printStackTrace();
        }

        return sbar;
    }

    /**
     * 获取banner图片占屏幕高度，单位pix
     * 
     * @return
     */
    public static int getBannerHeight() {
        int totalW = ScreenUtil.getScreenWidth();
        // 依据9*16进行计算
        return totalW * 9 / 16;
    }

    /**
     * 获得状态栏的高度
     *
     * @param context
     * @return
     */
    public static int getStatusHeight(Context context)
    {

        int statusHeight = -1;
        try
        {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return statusHeight;
    }

}