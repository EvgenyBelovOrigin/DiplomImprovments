package ru.netology.nework.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.netology.nework.dao.UserDao
import ru.netology.nework.dto.User
import ru.netology.nework.entity.UserEntity
import ru.netology.nework.repository.Repository
import ru.netology.nework.utils.SingleLiveEvent
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: Repository,
    private val dao: UserDao
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

    val checkedUsers: LiveData<List<User>> = repository.checkedUsers
        .asLiveData(Dispatchers.Default)


    // init with swipe refresh otherwise state of checked users doesn't works

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

    fun checkUser(user: User) {
        try {
            viewModelScope.launch {
                dao.insert(
                    if (user.isChecked) {
                        UserEntity.fromDto(
                            user.copy(isChecked = false)
                        )
                    } else {
                        UserEntity.fromDto(
                            user.copy(isChecked = true)
                        )
                    }
                )
            }
        } catch (e: Exception) {
            throw e
        }
    }

    fun makeAllUsersUnchecked() {
        try {
            viewModelScope.launch {
                dao.makeAllUsersUnchecked()
            }
        } catch (e: Exception) {
            throw e
        }
    }

    fun setCheckedUsers(listUsersIds: List<Int>) {
        listUsersIds.forEach { id ->
            try {
                viewModelScope.launch {
                    dao.checkById(id)
                }
            } catch (e: Exception) {
                throw e
            }

        }

    }

    fun chooseUser(authorId: Int?): User? =

        data.value?.find { it.id == authorId }

}