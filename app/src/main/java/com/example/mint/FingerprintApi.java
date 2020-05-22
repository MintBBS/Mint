package com.example.mint;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface FingerprintApi {
    @GET("fingerprint/{imei}")
    Call<User> validateFingerPrint(@Path("imei") String imei);
}
