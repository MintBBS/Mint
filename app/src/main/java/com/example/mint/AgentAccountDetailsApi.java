package com.example.mint;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface AgentAccountDetailsApi {
    @GET("getAccountDetails/{agentId}")
    Call<Account> getAgentDetails(@Path("agentId") String agentId);

}
