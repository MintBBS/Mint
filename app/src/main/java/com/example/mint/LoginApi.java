package com.example.mint;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface LoginApi {
    //login with fingerprint string
    @GET("login/{agentId}/{password}/{fingerprint}")
    Call<User> validateUser(@Path("agentId") String agentId, @Path("password") String password, @Path("fingerprint") String fingerprint);

    //login without fingerprint string
    @GET("login/{agentId}/{password}")
    Call<User> validateUserCredential(@Path("agentId") String agentId, @Path("password") String password);


    @GET("resetPassword/{agentId}/{password}")
    Call<Integer> resetPassword(@Path ("agentId") String agentId, @Path ("password") String password);

}
