package com.google.codelabs.mdc.java.shrine.api;

import com.google.codelabs.mdc.java.shrine.entities.RegisterForm;
import com.google.codelabs.mdc.java.shrine.entities.RentBikeRequest;
import com.google.codelabs.mdc.java.shrine.utils.Common;
import com.google.codelabs.mdc.java.shrine.utils.Constant;
import com.google.codelabs.mdc.java.shrine.entities.LoginForm;
import com.google.codelabs.mdc.java.shrine.entities.MyResponse;
import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    String baseUrl = "http://" + Constant.DOMAIN_IP + ":" + Constant.OPEN_PORT + "/";
    Gson gson = Common.getMyGson();


    OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
    .writeTimeout(60, TimeUnit.SECONDS)
    .readTimeout(60, TimeUnit.SECONDS)
    .build();

    ApiService apiService = new Retrofit.Builder()
//            .baseUrl("http://hongdatchy.me:8080/")
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()
            .create(ApiService.class);

    @POST("api/common/loginUser")
    Call<MyResponse> login(@Body LoginForm loginForm);

    @GET("api/common/logout/{token}")
    Call<MyResponse> logout(@Path("token") String token);

    @GET("api/common/station")
    Call<MyResponse> getAllStation();

    @GET("api/common/bike/{bikeId}")
    Call<MyResponse> findBikeInfoByBikeId(@Path("bikeId") int bikeId);

    @GET("api/common/city")
    Call<MyResponse> findAllCity();

    @GET("api/common/district/{cityId}")
    Call<MyResponse> findDistrictByCityId(@Path("cityId") int cityId);

    @GET("api/common/ward/{districtId}")
    Call<MyResponse> findWardByDistrictId(@Path("districtId") int districtId);

    @POST("api/common/register")
    Call<MyResponse> register(@Body RegisterForm registerForm);

    @GET("api/common/active/{code}")
    Call<MyResponse> active(@Path("code") String code);

    @GET("api/common/token/{token}")
    Call<MyResponse> checkLoginByToken(@Path("token") String token);

    @POST("api/us/rentBike")
    Call<MyResponse> rentBike(@Body RentBikeRequest rentBikeRequest, @Header("token") String token);

    @POST("api/us/continueRentBike")
    Call<MyResponse> continueRentBike(@Body int bikeId, @Header("token") String token);

    @POST("api/us/endRentBike")
    Call<MyResponse> endRentBike(@Body int bikeId, @Header("token") String token);

    @GET("api/us/contract")
    Call<MyResponse> getAllContractUser(@Header("token") String token);

    @GET("https://maps.googleapis.com/maps/api/directions/json")
    Call<Object> getDirection(@Query("origin") String origin,
                              @Query("destination") String destination,
                              @Query("mode") String mode,
                              @Query("key") String key);


}
