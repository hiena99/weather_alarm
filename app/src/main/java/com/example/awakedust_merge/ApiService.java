package com.example.awakedust_merge;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface ApiService {
    static final String BASEURL = "http://newsky2.kma.go.kr/service/SecndSrtpdFrcstInfoService2/";
    static final String APPKEY ="D52qWXciCPOPcnAqLGatrty1RvEA6TFHQjDKHPwV12LnJDRonDX9opXN1ZjnoJrtCc%2FRC%2BDAXJO0NwjE3vTDXw%3D%3D";
    @GET("ForecastGrib")
    Call<WeatherInfo> getData(@Query("ServiceKey") String key,
                              @Query("base_date") String base_data,
                              @Query("base_time") String base_time,
                              @Query("nx") String x,
                              @Query("ny") String y,
                              @Query("numOfRows") String rows,
                              @Query("_type") String type
                              );



}
