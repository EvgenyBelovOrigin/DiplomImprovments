package ru.netology.nework.repository



interface Repository {
    suspend fun getPosts()
}
