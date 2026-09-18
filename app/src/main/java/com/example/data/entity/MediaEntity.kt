package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "media_items")
data class MediaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: String, // "POST", "VIDEO", "SHORT"
    val title: String,
    val authorName: String,
    val authorHandle: String,
    val authorAvatarRes: String,
    val mediaRes: String,
    val duration: String, // e.g. "14:25", "0:45", or ""
    val viewsCount: Long,
    val likesCount: Long,
    val dislikesCount: Long,
    val commentsCount: Long,
    val caption: String,
    val tags: String,
    val audioTrack: String,
    val category: String, // "All", "Tech", "Travel", "Gaming", "Music", "Aesthetic"
    val isLiked: Boolean = false,
    val isDisliked: Boolean = false,
    val isSaved: Boolean = false,
    val isSubscribed: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
