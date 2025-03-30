package ru.netology.nework.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nework.dto.Attachment
import ru.netology.nework.dto.Post

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val authorId: Int,
    val author: String,
    val authorJob: String?,
    val authorAvatar: String?,
    val content: String,
    val published: String,
    val link: String?,
    val mentionIds: List<Int>?,
    val mentionedMe: Boolean,
    val likeOwnerIds: List<Int>?,
    val likedByMe: Boolean,
//    val users: UsersArray? // too many questions, but works //todo


    @Embedded
    val attachment: Attachment?,

//    @Embedded
//    val coords: Coordinates?,// todo


) {

    fun toDto() = Post(
        id,
        authorId,
        author,
        authorJob,
        authorAvatar,
        content,
        published,
        null,
        link,
        mentionIds,
        mentionedMe,
        likeOwnerIds,
        likedByMe,
        attachment,
        null,
    )

    companion object {

        fun fromDto(dto: Post) =
            PostEntity(
                dto.id,
                dto.authorId,
                dto.author,
                dto.authorJob,
                dto.authorAvatar,
                dto.content,
                dto.published,
                dto.link,
                dto.mentionIds,
                dto.mentionedMe,
                dto.likeOwnerIds,
                dto.likedByMe,
//    val users: UsersArray? // too many questions, but works
//                coords = dto.coords,
                attachment = dto.attachment,
            )

    }
}



