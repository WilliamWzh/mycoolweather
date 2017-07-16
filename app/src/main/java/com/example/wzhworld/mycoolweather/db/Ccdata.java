package com.example.wzhworld.mycoolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by wzhworld on 2017/7/14.
 */

public class Ccdata extends DataSupport{
    private String city;
    private String code;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
