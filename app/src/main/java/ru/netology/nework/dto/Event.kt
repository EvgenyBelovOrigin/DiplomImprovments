package ru.netology.nework.dto

data class Event(
    val id: Int,
    val authorId: Int,
    val author: String,
    val authorJob: String? = "",
    val authorAvatar: String? = "",
    val content: String,
    val datetime: String,
    val published: String,
    val coords: Coordinates? = null,
    val type: EventType,
    val likeOwnerIds: List<Int>,
    val likedByMe: Boolean,
    val speakerIds: List<Int>,
    val participantsIds: List<Int>,
    val participatedByMe: Boolean,
    val attachment: Attachment? = null,
    val link: String? = null,
    val users: Map<String, UserPreview>,
    val ownedByMe: Boolean = false,
    val isPlayingAudio:Boolean = false,
    val isPlayingAudioPaused:Boolean = false
)

enum class EventType {
    ONLINE,
    OFFLINE
}

