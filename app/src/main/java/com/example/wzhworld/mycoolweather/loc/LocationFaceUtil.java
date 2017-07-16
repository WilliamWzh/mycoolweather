package com.example.wzhworld.mycoolweather.loc;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.wzhworld.mycoolweather.db.Ccdata;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by wzhworld on 2017/7/16.
 */

public class LocationFaceUtil implements BDLocationListener {
    private LocationFace locationFace; // 这个为自己写的一个接口，用来回调给外部处理
    public LocationClient mLocationClient = null;
    private Context context;

    public LocationFaceUtil(Context context, LocationFace locationFace) {
        super();
        this.locationFace = locationFace;
        this.context = context;
        mLocationClient = new LocationClient(context);
        mLocationClient.registerLocationListener(LocationFaceUtil.this);//LocationFaceUtil.this
        startLocation();
    }

    private void startLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy); // 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setScanSpan(0); // 可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true); // 可选，设置是否需要地址信息，默认不需要
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }
    @Override
    public void onReceiveLocation(BDLocation arg0) {
//注意这里，一定要判断BdLocation的返回值，只有在getLocType（）==61或者161的情况下才表示定位成功，具体返回的错误码可参考http://lbsyun.baidu.com/index.php?title=android-locsdk/guide/ermsg
        if (arg0.getLocType() == 61 || arg0.getLocType() == 161 && arg0.getLatitude() != 0.0) {
            //将城市的名字存到SharedPreferences里面
            new ShareDB(context).save(DBConstants.CITY_NAME,arg0.getCity());
            //将定位结果回调给locationFace的locationResult（）方法
            locationFace.locationResult(arg0);
        }
    }

    @Override
    public void onConnectHotSpotMessage(String s, int i) {

    }
}

//mDistrict = bdLocation.getDistrict();
//        mDistrict = mDistrict.substring(0,mDistrict.length()-1);
//        List<Ccdata> ccdatas = DataSupport.where("city=?",mDistrict).find(Ccdata.class);
//        for(Ccdata ccdata:ccdatas) {
//        city = ccdata.getCity();
//        code = ccdata.getCode();
//        }