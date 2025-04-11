package ru.netology.nework.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import ru.netology.nework.dto.User
import ru.netology.nework.repository.Repository
import ru.netology.nmedia.auth.AppAuth
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: Repository,
    private val appAuth: AppAuth,
) : ViewModel() {

    val data: LiveData<List<User>> = repository.users
        .asLiveData(Dispatchers.Default)

    init {
        getAllUsers()
    }

    private fun getAllUsers() = viewModelScope.launch {
        try {
            repository.getAllUsers()
        } catch (e: Exception) {
            throw e
        }
    }


}