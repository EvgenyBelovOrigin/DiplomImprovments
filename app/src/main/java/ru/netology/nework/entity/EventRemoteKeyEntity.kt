package ru.netology.nework.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class EventRemoteKeyEntity(
    @PrimaryKey
    val type: KeyType,
    val key: Int,
) {
    enum class KeyType {
        AFTER,
        BEFORE
    }
}