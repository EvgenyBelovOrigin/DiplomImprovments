package ru.netology.nmedia.auth

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.netology.nework.api.ApiService
import ru.netology.nework.dto.Token
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppAuth @Inject constructor(
    @ApplicationContext
    private val context: Context,
) {
    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    private val _authState = MutableStateFlow<Token?>(Token(0, null))


    init {
        val id = prefs.getInt(ID_KEY, 0)
        val token = prefs.getString(TOKEN_KEY, null)

        if (id == 0 || token == null) {
            prefs.edit {
                clear()
                apply()
            }
        } else {
            _authState.value = Token(id, token)
        }
    }

    val authState: StateFlow<Token?> = _authState.asStateFlow()

    @InstallIn(SingletonComponent::class)
    @EntryPoint
    interface AppAuthEntryPoint {
        fun getApiService(): ApiService

    }

    @Synchronized
    fun setAuth(token: Token) {
        with(prefs.edit()) {
            putInt(ID_KEY, token.id)
            putString(TOKEN_KEY, token.token)
            apply()
        }
        _authState.value = token
    }

    @Synchronized
    fun clear() {
        with(prefs.edit()) {
            clear()
            commit()
        }
        _authState.value = Token(0, null)
    }


    companion object {
        const val TOKEN_KEY = "TOKEN_KEY"
        const val ID_KEY = "ID_KEY"

    }

}
