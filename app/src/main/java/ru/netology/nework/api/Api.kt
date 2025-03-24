package ru.netology.nework.api

import retrofit2.Response
import retrofit2.http.GET
import ru.netology.nework.dto.Post


interface ApiService {

    @GET("posts")
    suspend fun getPosts(): Response<List<Post>>
}
