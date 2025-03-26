package ru.netology.nework.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nework.dto.MediaUpload
import ru.netology.nework.repository.Repository
import ru.netology.nework.utils.SingleLiveEvent
import ru.netology.nmedia.model.PhotoModel
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val repository: Repository,
) : ViewModel() {
    val noAvatar = PhotoModel()
    val _signedUp = SingleLiveEvent<Unit>()
    val signedUp: LiveData<Unit>
        get() = _signedUp
    val _wrongPassConfirm = SingleLiveEvent<Unit>()
    val wrongPassConfirm: LiveData<Unit>
        get() = _wrongPassConfirm
    val _exception = SingleLiveEvent<Unit>()
    val exception: LiveData<Unit>
        get() = _exception

    private val _avatar = MutableLiveData<PhotoModel>(noAvatar)
    val avatar: LiveData<PhotoModel>
        get() = _avatar


    fun signUp(login: String, password: String, passConfirm: String, name: String) {
        if (password != passConfirm) {
            _wrongPassConfirm.value = Unit
        } else {

            viewModelScope.launch {
                try {
                    _avatar.value?.file?.let { file ->
                        repository.signUpWithAvatar(login, password, name, MediaUpload(file))
                    } ?: repository.signUp(login, password, name)

                    _signedUp.value = Unit

                } catch (e: Exception) {
                    _exception.value = Unit

                }
            }
        }


    }

    fun updateAvatar(uri: Uri, file: File) {
        _avatar.value = PhotoModel(uri, file)
    }

    fun clearAvatar() {
        _avatar.value = noAvatar
    }
}