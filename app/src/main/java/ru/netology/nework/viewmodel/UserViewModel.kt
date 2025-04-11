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
import ru.netology.nework.utils.SingleLiveEvent
import ru.netology.nmedia.auth.AppAuth
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: Repository,
) : ViewModel() {

    private val _onError = SingleLiveEvent<Unit>()
    val onError: LiveData<Unit>
        get() = _onError

    private val _onStopLoading = SingleLiveEvent<Unit>()
    val onStopLoading: LiveData<Unit>
        get() = _onStopLoading

    private val _onStartLoading = SingleLiveEvent<Unit>()
    val onStartLoading: LiveData<Unit>
        get() = _onStartLoading

    val data: LiveData<List<User>> = repository.users
        .asLiveData(Dispatchers.Default)

    init {
        getAllUsers()
    }

    fun getAllUsers() = viewModelScope.launch {
        try {
            _onStartLoading.value = Unit
            repository.getAllUsers()
            _onStopLoading.value = Unit
        } catch (e: Exception) {
            _onError.value = Unit
        }
    }


}