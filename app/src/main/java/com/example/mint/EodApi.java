package com.example.mint;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface EodApi {
    @GET("eod")
    Call<List<AgentTransaction>> getEodReport();

}
