package ru.netology.nework.dto

data class Post(
    val id: Int,
    val authorId: Int,
    val author: String,
    val authorJob: String?,
    val authorAvatar: String?,
    val content: String,
    val published: String,
    val coords: Coordinates?,
    val link: String?,
    val mentionIds: List<Int>?,
    val mentionedMe: Boolean,
    val likeOwnerIds: List<Int>?,
    val likedByMe: Boolean,
    val attachment: Attachment?,
    val users: Map<String, UserPreview?>?, // too many questions, but works
    val ownedByMe: Boolean = false,
    val isPlayingAudio:Boolean = false,
    val isPlayingAudioPaused:Boolean = false
)

data class UserPreview(
    val name: String?,
    val avatar: String?
)


data class Coordinates(
    val lat: Double,
    val long: Double,
)


data class Attachment(
    val url: String,
    val type: AttachmentType,
)

enum class AttachmentType {
    IMAGE,
    VIDEO,
    AUDIO,
}

