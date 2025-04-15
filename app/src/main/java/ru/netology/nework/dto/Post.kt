package ru.netology.nework.dto

sealed interface FeedItem {
    val id: Int
}

data class Post(
    override val id: Int,
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
    val users: Map<String, UserPreview?>?,
    val ownedByMe: Boolean = false,
    val isPlayingAudio: Boolean = false,
    val isPlayingAudioPaused: Boolean = false
) : FeedItem

data class UserAvatar(
    override val id: Int,
    val url: String
) : FeedItem


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
    var type: AttachmentType,
)

enum class AttachmentType {
    IMAGE,
    VIDEO,
    AUDIO,
}

