package com.example.mint;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface DepositApi {
    @GET("deposit/{customerAadharNo}/{customerAccountNo}/{amount}/{agentId}")
    Call<Transaction> depositMoney(@Path("customerAadharNo") String customerAadharNo, @Path("customerAccountNo") String customerAccountNo, @Path("amount") String amount, @Path("agentId") String agentId);

}
