package com.example.mint;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface MiniStatementApi {
  @GET("miniStatement/{aadharNo}/{accountNo}")
  Call<List<Transaction>> getMiniStatement(@Path("aadharNo") String aadharNo, @Path("accountNo") String accountNo);

  // --------------------- card ----------------------
  @GET("cMiniStatement/{cardNumber}/{cardHolderName}/{cvv}/{expireDate}/{pin}")
  Call<List<Transaction>> getminiStatement(@Path("cardNumber") String cardNumber, @Path("cardHolderName") String cardHolderName , @Path("cvv") String cvv, @Path("expireDate") String expireDate, @Path("pin") String pin);


}
