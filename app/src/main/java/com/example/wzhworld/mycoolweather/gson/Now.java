package com.example.wzhworld.mycoolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wzhworld on 2017/6/1.
 */

public class Now {
    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More more;

    public class More{
        @SerializedName("txt")
        public String info;
    }
}
