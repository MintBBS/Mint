package com.example.mint;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface SeedingApi {
    @GET("mobileSeeding/{customerAadhar}/{number}")
    Call<Integer> updateNumber(@Path("customerAadhar") String customerAadhar, @Path ("number") String number);
}
