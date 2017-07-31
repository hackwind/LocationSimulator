package com.tonyhu.location.util;

/**
 * Created by Administrator on 2017/6/6.
 */

public class Constants {
    //腾讯广点通参数
    public final static String GDT_APPID = "1101152570";
    public final static String GDT_SPLASH_POS_ID = "1070720495106046";
    public final static String GDT_PAGE_FAVORITE = "7010221455708097";//"9079537218417626401";
    public final static String GDT_PAGE_FEEDBACK= "6090425445707008";
    public final static String GDT_PAGE_MAIN = "9070921405704296";
    public final static String GDT_PAGE_SHENYOU = "9000729415407238";
    public final static String GDT_PAGE_SEARCH = "7030023485200381";

    //点击穿越或神游计数类型
    public final static int TYPE_CHUANYUE = 1;//穿越
    public final static int TYPE_SHENYOU = 2;//神游

    //免费最多穿越和神游次数
    public final static int MAX_CHUANYUE_TIMES = 5;
    public final static int MAX_SHENYOU_TIMES = 2;

    //避免市场审核不通过（判断有意引导点击广告导致不通过），设定一个启用穿越和神游计数的起始日期
    public static final String TEST_END_TIME = "2017-08-05";
}
