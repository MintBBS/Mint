package com.example.mint;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface BalanceEnquiryApi {
    @GET("balance/{aadharNo}/{accountNo}")
    Call<Account> getBalaceByAccount(@Path("aadharNo") String aadharNo, @Path("accountNo") String accountNo);

    //----------- card ---------------
    @GET("cBalanceEnquiry/{cardNumber}/{cardHolderName}/{cvv}/{expireDate}/{pin}")
    Call<Account> getRupayBalanceByAccount(@Path("cardNumber") String cardNumber, @Path("cardHolderName") String cardHolderName , @Path("cvv") String cvv, @Path("expireDate") String expireDate, @Path("pin") String pin);

}
