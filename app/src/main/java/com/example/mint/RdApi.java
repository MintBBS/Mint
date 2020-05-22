package com.example.mint;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.GET;

import retrofit2.http.Path;



public interface RdApi {
    @POST("rdInsert")
    Call<Rd> createRd(@Body Rd rd);

    @GET("getRd/{aadhar_number}")
    Call<Rd> getAadhar_nbr(@Path("aadhar_number") String aadhar_number);
}
