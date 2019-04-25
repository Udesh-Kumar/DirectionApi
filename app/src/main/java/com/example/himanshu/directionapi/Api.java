package com.example.himanshu.directionapi;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

public interface Api {
    @GET("maps/api/directions/json?")
    Call<Map> placedata(@QueryMap HashMap<String,String> data);

}
