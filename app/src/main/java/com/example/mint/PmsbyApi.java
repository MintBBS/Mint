package com.example.mint;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface PmsbyApi {

    @POST("pmsbyInsert")
    Call<Pmsby> createPmsby(@Body Pmsby pmsby);

   @GET("getPmsby/{aadhar_number}")
    Call<Pmsby> getAadharnbr(@Path("aadhar_number") String aadhar_number);
}
