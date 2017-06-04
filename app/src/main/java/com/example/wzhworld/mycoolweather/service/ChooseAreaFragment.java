package com.example.wzhworld.mycoolweather.service;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.icu.text.LocaleDisplayNames;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wzhworld.mycoolweather.R;
import com.example.wzhworld.mycoolweather.WeatherActivity;
import com.example.wzhworld.mycoolweather.db.DbCity;
import com.example.wzhworld.mycoolweather.db.DbCounty;
import com.example.wzhworld.mycoolweather.db.DbProvince;
import com.example.wzhworld.mycoolweather.util.HttpUtil;
import com.example.wzhworld.mycoolweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by wzhworld on 2017/5/30.
 */

public class ChooseAreaFragment extends Fragment {
    private TextView titleView;
    private Button backButton;
    private ListView showListView;
    private ProgressDialog mProgressDialog;

    public final static int LEVEL_PROVINCE = 0;
    public final static int LEVEL_CITY = 1;
    public final static int LEVEL_COUNTY = 2;

    private ArrayAdapter adapter;
    private List<String> dataList = new ArrayList<>();


    private List<DbProvince> provinceList;
    private List<DbCity> cityList;
    private List<DbCounty> countyList;

    private DbProvince selectedProvince;
    private DbCity selectedCity;

    private int currentLevel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area,container,false);
        titleView = (TextView)view.findViewById(R.id.title_text);
        backButton = (Button)view.findViewById(R.id.back_button);
        showListView = (ListView)view.findViewById(R.id.list_view);
        adapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1,dataList);
        showListView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        showListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currentLevel == LEVEL_PROVINCE){
                    selectedProvince = provinceList.get(position);
                    queryCities();
                }
                else if(currentLevel== LEVEL_CITY){
                    selectedCity = cityList.get(position);
                    queryCounties();
                }else if(currentLevel == LEVEL_COUNTY){
                    String weatherId = countyList.get(position).getWeatherId();
                    Log.d("Choose",weatherId);
                    Intent intent = new Intent(getActivity(), WeatherActivity.class);
                    intent.putExtra("weather_id",weatherId);
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentLevel == LEVEL_COUNTY){
                    queryCities();
                }else if(currentLevel == LEVEL_CITY){
                    queryProvinces();
                }
            }
        });
        queryProvinces();
    }
    private void queryProvinces(){
        titleView.setText("中国");
        backButton.setVisibility(View.GONE);
        provinceList = DataSupport.findAll(DbProvince.class);
        if(provinceList.size() > 0){
            dataList.clear();
            for(DbProvince province:provinceList){
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            showListView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        }
        else{
            String address = "http://guolin.tech/api/china/";
            queryFromServer(address,"province");
        }
    }
    private void queryCities(){
        titleView.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("provinceid = ?",String.valueOf
                (selectedProvince.getId())).find(DbCity.class);
        if(cityList.size()>0){
            dataList.clear();
            for(DbCity city:cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            showListView.setSelection(0);
            currentLevel = LEVEL_CITY;
        }
        else{
            int provinceCode = selectedProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china/"+provinceCode;
//            Log.d("flag",address);
            queryFromServer(address,"city");
        }
    }
    private void queryCounties(){
        titleView.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countyList = DataSupport.where("cityid=?", String.valueOf
                (selectedCity.getId())).find(DbCounty.class);
        if(countyList.size()>0){
            dataList.clear();
            for(DbCounty county:countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            showListView.setSelection(0);
            currentLevel=LEVEL_COUNTY;
        }
        else{
            int provinceCode = selectedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            String address = "http://guolin.tech/api/china/"+provinceCode+"/"+cityCode;
            queryFromServer(address,"county");
        }
    }
    private void queryFromServer(String address,final String type){
        showProgressDialog();
        HttpUtil.sendOKHttpRequest(address, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();

                boolean result = false;
                if("province".equals(type)){
                    result = Utility.handleProvince(responseText);
                }else if("city".equals(type)){
                    result = Utility.handleCity(responseText,selectedProvince.getId());
                }else if("county".equals(type)){
                    result = Utility.handleCounty(responseText,selectedCity.getId());
                }
                if(result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if("province".equals(type)){
                                queryProvinces();
                            }else if("city".equals(type)){
                                queryCities();
                            }else if("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }
            }
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }


        });
    }

    private void closeProgressDialog() {
        if(mProgressDialog != null){
            mProgressDialog.dismiss();
        }
    }

    private void showProgressDialog() {
        if(mProgressDialog == null){
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage("正在加载");
            mProgressDialog.setCanceledOnTouchOutside(false);
        }
        mProgressDialog.show();
    }
}
