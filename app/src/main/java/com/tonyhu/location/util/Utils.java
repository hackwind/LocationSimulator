package com.tonyhu.location.util;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2017/7/29/029.
 */

public class Utils {
    //判断是否在指定的测试日期范围之后，
    public static boolean timeAfterTest() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = format.parse(Constants.TEST_END_TIME,new ParsePosition(0));
        long time = date.getTime();
        return System.currentTimeMillis() > time;
    }
}
