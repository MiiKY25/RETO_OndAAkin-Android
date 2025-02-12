package com.example.ondaakin4;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit = null;

    public static LoginService getLoginService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("http://10.0.2.2:5000/") // Para acceder al localhost en el emulador
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(LoginService.class);
    }
}
