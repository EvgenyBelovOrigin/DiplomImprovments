package ru.netology.nework.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nework.dto.Event

@Entity
data class EventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val login: String,
    val name: String,
    val avatar: String?
) {

    fun toDto() = Event(
        id,
        login,
        name,
        avatar
    )

    companion object {

        fun fromDto(dto: Event) =
            EventEntity(
                dto.id,
                dto.login,
                dto.name,
                dto.avatar,
            )
    }
}



