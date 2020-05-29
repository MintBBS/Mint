package com.example.mint;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AccountApi {
    @POST("create")
    Call<BankResponse> createAccount(@Body AccountDetails account);
}
