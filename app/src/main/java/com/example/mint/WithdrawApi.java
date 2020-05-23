package com.example.mint;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface WithdrawApi {
    @GET("withdraw/{customerAadharNo}/{customerAccountNo}/{amount}/{agentId}")
    Call<Transaction> withdrawMoney(@Path("customerAadharNo") String customerAadharNo, @Path("customerAccountNo") String customerAccountNo, @Path("amount") String amount, @Path("agentId") String agentId);

    @GET("rupaywithdraw/{agentId}/{cardNumber}/{cardHolderName}/{cvv}/{expireDate}/{pin}/{amount}")
    Call<Transaction> rupayWithdraw(@Path("agentId") String agentId,@Path("cardNumber") String cardNumber,@Path("cardHolderName") String cardHolderName , @Path("cvv") String cvv, @Path("expireDate") String expireDate,@Path("pin") String pin,@Path("amount") String amount);

}
