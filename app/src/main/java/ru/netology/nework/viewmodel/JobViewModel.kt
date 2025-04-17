package ru.netology.nework.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.nework.dto.Job
import ru.netology.nework.repository.Repository
import ru.netology.nework.utils.SingleLiveEvent
import ru.netology.nmedia.auth.AppAuth
import javax.inject.Inject

@HiltViewModel
class JobViewModel @Inject constructor(
    private val repository: Repository,
    appAuth: AppAuth,


    ) : ViewModel() {

    private val _onError = SingleLiveEvent<Unit>()
    val onError: LiveData<Unit>
        get() = _onError

    @OptIn(ExperimentalCoroutinesApi::class)
    val data: LiveData<List<Job>> = appAuth.authState
        .flatMapLatest { token ->
            repository.jobs.map { job ->
                job.map { it.copy(ownedByMe = it.id == token?.id) }
            }
        }.asLiveData()


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

    fun deleteJob(job: Job) {
        //todo

    }


}