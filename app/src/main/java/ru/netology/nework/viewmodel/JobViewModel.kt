package ru.netology.nework.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.netology.nework.dao.UserDao
import ru.netology.nework.dto.Job
import ru.netology.nework.repository.Repository
import ru.netology.nework.utils.SingleLiveEvent
import javax.inject.Inject

@HiltViewModel
class JobViewModel @Inject constructor(
    private val repository: Repository,
    private val dao: UserDao
) : ViewModel() {

    private val _onError = SingleLiveEvent<Unit>()
    val onError: LiveData<Unit>
        get() = _onError

    val data: LiveData<List<Job>> = repository.jobs
        .asLiveData(Dispatchers.Default)


    // init with swipe refresh otherwise state of checked users doesn't works

//    init {
//        getAllUsers()
//    }

    fun getAllJobs(userId: Int) = viewModelScope.launch {
        try {
            repository.getAllJobs(userId)
        } catch (e: Exception) {
            _onError.value = Unit
        }
    }


}