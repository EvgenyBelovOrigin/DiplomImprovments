package ru.netology.nework.dto

data class Post(
    val id: Int,
    val authorId: Int,
    val author: String,
    val authorJob: String,
    val authorAvatar: String?,
    val content: String,
    val published: String,
    val coords: Coordinates? = null,
    val link: String? = null,
    val mentionIds: List<Int>? = null,
    val mentionedMe: Boolean,
    val likeOwnerIds: List<Int>? = null,
    val likedByMe: Boolean,
    val attachment: Attachment? = null,
    //val users: List<Users>?
)

data class Users(
    val id: Int,
    val userPreview: UserPreview?,
)

data class UserPreview(
    val name: String,
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

