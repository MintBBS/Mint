package com.example.mint;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApyApi {
    @POST("apyInsert")
    Call<Apy> createApy(@Body Apy apy);

    @GET("getApy/{aadhar_number}")
    Call<Apy> getAadhar_number(@Path("aadhar_number") String aadhar_number);
}

