package com.example.mint;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.GET;
import retrofit2.http.Path;


public interface PmjjbyApi {
    @POST("pmjjbyInsert")
    Call<Pmjjby> createPmjjby(@Body Pmjjby pmjjby);

    @GET("getPmjjby/{aadhar_number}")
    Call<Pmjjby> getAadharnumber(@Path("aadhar_number") String aadhar_number);
}
