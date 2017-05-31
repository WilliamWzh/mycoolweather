package com.example.wzhworld.mycoolweather.util;

import android.text.TextUtils;

import com.example.wzhworld.mycoolweather.db.DbCity;
import com.example.wzhworld.mycoolweather.db.DbCounty;
import com.example.wzhworld.mycoolweather.db.DbProvince;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by wzhworld on 2017/5/22.
 */

public class Utility {
    public static boolean handleProvince(String response){
        if(!TextUtils.isEmpty(response)){
            try{
                JSONArray allProvince = new JSONArray(response);
                for(int i = 0;i<allProvince.length();i++){
                    JSONObject provinceObject = allProvince.getJSONObject(i);
                    DbProvince province = new DbProvince();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    public static boolean handleCity(String response,int provinceId){
        if (!TextUtils.isEmpty(response)){
            try{
                JSONArray allCity = new JSONArray(response);
                for(int i = 0;i<allCity.length();i++){
                    JSONObject cityObject = allCity.getJSONObject(i);
                    DbCity city = new DbCity();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    public static boolean handleCounty(String response,int cityId){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray allCounty = new JSONArray(response);
                for(int i=0;i<allCounty.length();i++){
                    JSONObject countyObject = allCounty .getJSONObject(i);
                    DbCounty county = new DbCounty();
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
