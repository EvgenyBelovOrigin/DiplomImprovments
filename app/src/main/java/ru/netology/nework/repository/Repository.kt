package ru.netology.nework.repository

import ru.netology.nework.dto.MediaUpload


interface Repository {
    suspend fun getPosts()
    suspend fun signIn(login: String, password: String)
    suspend fun signUp(login: String, password: String, name: String)
    suspend fun signUpWithAvatar(login: String, password: String, name: String, upload: MediaUpload)
}
