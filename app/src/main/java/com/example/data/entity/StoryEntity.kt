package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stories")
data class StoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val authorName: String,
    val authorHandle: String,
    val authorAvatarRes: String,
    val storyMediaRes: String,
    val caption: String,
    val isViewed: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
