package com.example.mint;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface AadharApi {
    @GET("aadhar/{number}")
    Call<Aadhar> getAadharDetails(@Path("number") String number);
}
