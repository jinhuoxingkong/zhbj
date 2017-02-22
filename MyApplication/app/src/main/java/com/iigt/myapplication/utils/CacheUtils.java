package com.iigt.myapplication.utils;

import android.content.Context;

/**
 * Created by zhouheng on 2017/2/19.
 */

public class CacheUtils {

    public static void setCache(Context ctx, String url, String json){
        PrefUtils.setString(ctx, url, json);
    }

    public static String getCache(Context ctx, String url){
        return PrefUtils.getString(ctx, url, null);
    }
}
