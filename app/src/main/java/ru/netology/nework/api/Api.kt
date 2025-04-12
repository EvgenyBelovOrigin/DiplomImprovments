package ru.netology.nework.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import ru.netology.nework.dto.Event
import ru.netology.nework.dto.Media
import ru.netology.nework.dto.Post
import ru.netology.nework.dto.Token
import ru.netology.nework.dto.User


interface ApiService {
    //POSTS
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

    @Multipart
    @POST("users/registration")
    suspend fun signUpWithAvatar(
        @Part("login") login: RequestBody,
        @Part("pass") pass: RequestBody,
        @Part("name") name: RequestBody,
        @Part media: MultipartBody.Part,
    ): Response<Token>

    @GET("posts/{id}/before")
    suspend fun getBefore(
        @Path("id") id: Int,
        @Query("count") count: Int,
    ): Response<List<Post>>

    @GET("posts/latest")
    suspend fun getLatest(@Query("count") count: Int): Response<List<Post>>

    @GET("posts/{id}/after")
    suspend fun getAfter(
        @Path("id") id: Int,
        @Query("count") count: Int,
    ): Response<List<Post>>

    @POST("posts/{id}/likes")
    suspend fun likeById(@Path("id") id: Int): Response<Post>

    @DELETE("posts/{id}/likes")
    suspend fun disLikeById(@Path("id") id: Int): Response<Post>

    @POST("posts")
    suspend fun save(@Body post: Post): Response<Post>

    @Multipart
    @POST("media")
    suspend fun upload(@Part media: MultipartBody.Part): Response<Media>

    @DELETE("posts/{id}")
    suspend fun removePostById(@Path("id") id: Int): Response<Unit>

    //EVENTS

    @GET("events/{id}/after")
    suspend fun getEventsAfter(
        @Path("id") id: Int,
        @Query("count") count: Int
    ): Response<List<Event>>


    @GET("events/{id}/before")
    suspend fun getEventsBefore(
        @Path("id") id: Int,
        @Query("count") count: Int
    ): Response<List<Event>>

    @GET("events/latest")
    suspend fun getEventsLatest(@Query("count") count: Int): Response<List<Event>>

    @POST("events/{id}/likes")
    suspend fun likeEventById(@Path("id") id: Int): Response<Event>

    @DELETE("events/{id}/likes")
    suspend fun disLikeEventById(@Path("id") id: Int): Response<Event>

    @POST("events")
    suspend fun saveEvent(@Body event: Event): Response<Event>

    @DELETE("events/{id}")
    suspend fun removeEventById(@Path("id") id: Int): Response<Unit>


    //USERS

    @GET("users")
    suspend fun getAllUsers(): Response<List<User>>

}
