package com.commonmodule.mi.utils;

import android.util.Log;

/**
 * Created by mind on 2/10/15.
 */
public class LogUtil {

    private static String TAG_LOG = LogUtil.class.getSimpleName();

    public static boolean isLogEnable = true;

    public static void syso(Object o)
    {
        if (isLogEnable)
        {
            System.out.println(o);
        }
    }

    public static void e(String s)
    {
        if (isLogEnable)
        {
            Log.e(TAG_LOG, s);
        }
    }

    public static void w(String s)
    {
        if (isLogEnable)
        {
            Log.w(TAG_LOG, s);
        }
    }

    public static void i(String s)
    {
        if (isLogEnable)
        {
            Log.i(TAG_LOG, s);
        }
    }

}
