package com.example.mint;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface FundsTransferApi {

        @GET("fundTransfer/{customerAadhar}/{amount}/{customerAccount}/{bAccount}/{bAadhar}")
        Call<Transaction> transferMoney(@Path("customerAadhar") String customerAadhar, @Path("amount") String amount, @Path("customerAccount") String customerAccount, @Path("bAccount") String bAccount, @Path("bAadhar") String bAadhar);
    }
