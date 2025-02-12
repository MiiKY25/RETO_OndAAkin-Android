package com.example.ondaakin4;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LoginService {
    @POST("/login")
    Call<Respuesta> login(@Body Usuario usuario);
}
