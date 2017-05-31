package com.example.wzhworld.mycoolweather.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by wzhworld on 2017/5/22.
 */

public class HttpUtil {
    public static void sendOKHttpRequest(String address,okhttp3.Callback callback){
        OkHttpClient client  = new OkHttpClient();
        Request request= new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }
}
