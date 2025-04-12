package ru.netology.nework.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nework.dto.Attachment
import ru.netology.nework.dto.Event
import ru.netology.nework.dto.EventType
import ru.netology.nework.dto.UserPreview

@Entity
data class EventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val authorId: Int,
    val author: String,
    val authorJob: String? = "",
    val authorAvatar: String? = "",
    val content: String,
    val datetime: String,
    val published: String,
//    val coords: Coordinates? = null,
    val eventType: EventType,
    val likeOwnerIds: List<Int>,
    val likedByMe: Boolean,
    val speakerIds: List<Int>,
    val participantsIds: List<Int>,
    val participatedByMe: Boolean,
    @Embedded
    val attachment: Attachment? = null,
    val link: String? = null,
    val users: Map<String, UserPreview>,
    val ownedByMe: Boolean = false,
    val isPlayingAudio: Boolean = false,
    val isPlayingAudioPaused: Boolean = false,
) {

    fun toDto() = Event(
        id,
        authorId,
        author,
        authorJob,
        authorAvatar,
        content,
        datetime,
        published,
        null,
        eventType,
        likeOwnerIds,
        likedByMe,
        speakerIds,
        participantsIds,
        participatedByMe,
        attachment,
        link,
        users,
        ownedByMe,
        isPlayingAudio,
        isPlayingAudioPaused,
    )

    companion object {

        fun fromDto(dto: Event) =
            EventEntity(
                dto.id,
                dto.authorId,
                dto.author,
                dto.authorJob,
                dto.authorAvatar,
                dto.content,
                dto.datetime,
                dto.published,
                dto.type,
                dto.likeOwnerIds,
                dto.likedByMe,
                dto.speakerIds,
                dto.participantsIds,
                dto.participatedByMe,
                dto.attachment,
                dto.link,
                dto.users,
                dto.ownedByMe,
                dto.isPlayingAudio,
                dto.isPlayingAudioPaused,
            )
    }
}



