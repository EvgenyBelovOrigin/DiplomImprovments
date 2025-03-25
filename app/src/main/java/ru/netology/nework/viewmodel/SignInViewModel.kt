package ru.netology.nework.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nework.repository.Repository
import ru.netology.nework.utils.SingleLiveEvent
import ru.netology.nmedia.error.RunTimeError
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val repository: Repository,
) : ViewModel() {

    val _signedIn = SingleLiveEvent<Unit>()
    val signedIn: LiveData<Unit>
        get() = _signedIn
    val _notFoundException = SingleLiveEvent<Unit>()
    val notFoundException: LiveData<Unit>
        get() = _notFoundException
    val _exception = SingleLiveEvent<Unit>()
    val exception: LiveData<Unit>
        get() = _exception


    fun signIn(login: String, password: String) {
        viewModelScope.launch {
            try {
                repository.signIn(login, password)
                _signedIn.value = Unit

            } catch (
                e: RunTimeError,
            ) {
                _notFoundException.value = Unit
            } catch (e: Exception) {
                _exception.value = Unit

            }
        }

    }
}