package ru.netology.nework.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nework.dto.User

@Entity
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val login: String,
    val name: String,
    val avatar: String?,
    val isChecked: Boolean
) {

    fun toDto() = User(
        id,
        login,
        name,
        avatar,
        isChecked
    )

    companion object {

        fun fromDto(dto: User) =
            UserEntity(
                dto.id,
                dto.login,
                dto.name,
                dto.avatar,
                dto.isChecked
            )
    }
}
fun List<UserEntity>.toDto(): List<User> = map(UserEntity::toDto)
fun List<User>.toEntity(): List<UserEntity> = map(UserEntity.Companion::fromDto)



