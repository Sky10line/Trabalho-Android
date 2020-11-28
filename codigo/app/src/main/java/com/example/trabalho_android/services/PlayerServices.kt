package com.example.trabalho_android.services

import com.example.trabalho_android.models.Player
import retrofit2.Call
import retrofit2.http.GET

interface PlayerServices {
    @GET("/android/rest/player")
    fun list(): Call<List<Player>>;
}