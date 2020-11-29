package com.example.trabalho_android.services

import com.example.trabalho_android.models.Player
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PlayerServices {
    @GET("/Player")
    fun list(): Call<List<Player>>

    @POST("/Player")
    fun create(@Body player: Player): Call<Player>
}