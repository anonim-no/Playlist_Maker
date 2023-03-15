package com.example.playlistmaker.search

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface API {
    @GET("/search?entity=song")
    fun search(@Query("term") text: String): Call<SearchResponse>
}