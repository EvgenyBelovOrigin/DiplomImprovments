package ru.netology.nework.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nework.dto.Job

@Entity
data class JobEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val position: String,
    val start: String,
    val finish: String?,
    val link: String?,
    val ownedByMe: Boolean,
    val userId: Int
) {

    fun toDto() = Job(
        id,
        name,
        position,
        start,
        finish,
        link,
        ownedByMe,
        userId
    )

    companion object {

        fun fromDto(dto: Job) =
            JobEntity(
                dto.id,
                dto.name,
                dto.position,
                dto.start,
                dto.finish,
                dto.link,
                dto.ownedByMe,
                dto.userId
            )
    }
}

fun List<JobEntity>.toDto(): List<Job> = map(JobEntity::toDto)
fun List<Job>.toEntity(userId: Int): List<JobEntity> {
    return map { job ->
        JobEntity(
            job.id,
            job.name,
            job.position,
            job.start,
            job.finish,
            job.link,
            false,
            userId
        )
    }

}


