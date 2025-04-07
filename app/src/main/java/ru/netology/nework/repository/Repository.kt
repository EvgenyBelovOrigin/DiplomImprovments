package ru.netology.nework.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.nework.dto.MediaUpload
import ru.netology.nework.dto.Post
import ru.netology.nework.entity.PostEntity


interface Repository {
    // POSTS
    val posts: Flow<PagingData<Post>>

    suspend fun getPosts()
    suspend fun signIn(login: String, password: String)
    suspend fun signUp(login: String, password: String, name: String)
    suspend fun signUpWithAvatar(login: String, password: String, name: String, upload: MediaUpload)
    suspend fun disLikeById(id: Int)
    suspend fun likeById(id: Int)
}
