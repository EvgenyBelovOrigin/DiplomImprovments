package ru.netology.nework.api

import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import ru.netology.nework.dto.Post
import ru.netology.nework.dto.Token


interface ApiService {

    @GET("posts")
    suspend fun getPosts(): Response<List<Post>>

    @FormUrlEncoded
    @POST("users/authentication")
    suspend fun signIn(
        @Field("login") login: String,
        @Field("pass") pass: String,
    ): Response<Token>

    @FormUrlEncoded
    @POST("users/registration")
    suspend fun signUp(
        @Field("login") login: String,
        @Field("pass") pass: String,
        @Field("name") name: String,
    ): Response<Token>
}
