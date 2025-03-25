package ru.netology.nework.repository



interface Repository {
    suspend fun getPosts()
    suspend fun signIn(login: String, password: String)
}
