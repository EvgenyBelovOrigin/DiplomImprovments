package ru.netology.nework.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nework.dto.Job
import ru.netology.nework.repository.Repository
import ru.netology.nework.utils.SingleLiveEvent
import ru.netology.nmedia.error.RunTimeError
import javax.inject.Inject

@HiltViewModel
class NewJobViewModel @Inject constructor(
    private val repository: Repository,
) : ViewModel() {

    val _addedJob = SingleLiveEvent<Unit>()
    val addedJob: LiveData<Unit>
        get() = _addedJob
    val _notFoundException = SingleLiveEvent<Unit>()
    val notFoundException: LiveData<Unit>
        get() = _notFoundException
    val _exception = SingleLiveEvent<Unit>()
    val exception: LiveData<Unit>
        get() = _exception


    fun addJob(job: Job) {
        viewModelScope.launch {
            try {
                repository.addJob(job)
                _addedJob.value = Unit

            } catch (
                e: RunTimeError,
            ) {
                _notFoundException.value = Unit
            } catch (e: Exception) {
//                _exception.value = Unit
                throw e
            }
        }

    }
}