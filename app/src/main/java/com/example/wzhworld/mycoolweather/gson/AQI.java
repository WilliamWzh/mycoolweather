package com.example.wzhworld.mycoolweather.gson;

/**
 * Created by wzhworld on 2017/6/1.
 */

public class AQI {
    public AQICity city;
    public class AQICity{
        public String aqi;
        public String pm25;
    }
}
