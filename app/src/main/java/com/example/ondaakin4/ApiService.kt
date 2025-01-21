package com.example.ondaakin4

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @Multipart
    @POST("/upload/") // Cambia este endpoint por el que tengas en Django
    suspend fun uploadImage(
        @Part image: MultipartBody.Part
    ): Response<ResponseBody>
}
