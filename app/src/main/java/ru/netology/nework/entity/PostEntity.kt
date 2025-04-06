package ru.netology.nework.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nework.dto.Attachment
import ru.netology.nework.dto.Post
import ru.netology.nework.dto.UserPreview

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
    val users: Map<String,UserPreview?>?,


    @Embedded
    val attachment: Attachment?,
    val isPlayingAudio:Boolean = false,
    val isPlayingAudioPaused:Boolean = false,


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
        users,
        isPlayingAudio = isPlayingAudio,
        isPlayingAudioPaused = isPlayingAudioPaused
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
                dto.users,
                attachment = dto.attachment,
                isPlayingAudio = dto.isPlayingAudio,
                isPlayingAudioPaused = dto.isPlayingAudioPaused
            )

    }
}



