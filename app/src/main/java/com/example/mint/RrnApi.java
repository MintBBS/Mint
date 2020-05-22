package com.example.mint;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RrnApi {
    @GET("getByRrn/{rrn}")
    Call<AgentTransaction> getTransactionDetails(@Path("rrn") String rrn);

}
