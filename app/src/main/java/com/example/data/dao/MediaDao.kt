package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.MediaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaDao {
    @Query("SELECT * FROM media_items ORDER BY timestamp DESC")
    fun getAllMedia(): Flow<List<MediaEntity>>

    @Query("SELECT * FROM media_items WHERE type = :type ORDER BY timestamp DESC")
    fun getMediaByType(type: String): Flow<List<MediaEntity>>

    @Query("SELECT * FROM media_items WHERE category = :category ORDER BY timestamp DESC")
    fun getMediaByCategory(category: String): Flow<List<MediaEntity>>

    @Query("SELECT * FROM media_items WHERE isSubscribed = 1 ORDER BY timestamp DESC")
    fun getSubscribedMedia(): Flow<List<MediaEntity>>

    @Query("SELECT * FROM media_items WHERE isSaved = 1 ORDER BY timestamp DESC")
    fun getSavedMedia(): Flow<List<MediaEntity>>

    @Query("SELECT * FROM media_items WHERE id = :id LIMIT 1")
    fun getMediaById(id: Long): Flow<MediaEntity?>

    @Query("SELECT * FROM media_items WHERE title LIKE '%' || :query || '%' OR caption LIKE '%' || :query || '%' OR authorName LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchMedia(query: String): Flow<List<MediaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedia(item: MediaEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<MediaEntity>)

    @Update
    suspend fun updateMedia(item: MediaEntity)

    @Query("UPDATE media_items SET isLiked = :isLiked, likesCount = :likesCount WHERE id = :id")
    suspend fun updateLike(id: Long, isLiked: Boolean, likesCount: Long)

    @Query("UPDATE media_items SET isDisliked = :isDisliked, dislikesCount = :dislikesCount WHERE id = :id")
    suspend fun updateDislike(id: Long, isDisliked: Boolean, dislikesCount: Long)

    @Query("UPDATE media_items SET isSaved = :isSaved WHERE id = :id")
    suspend fun updateSave(id: Long, isSaved: Boolean)

    @Query("UPDATE media_items SET isSubscribed = :isSubscribed WHERE authorHandle = :authorHandle")
    suspend fun updateSubscriptionByAuthor(authorHandle: String, isSubscribed: Boolean)

    @Query("UPDATE media_items SET viewsCount = viewsCount + 1 WHERE id = :id")
    suspend fun incrementViews(id: Long)

    @Query("UPDATE media_items SET commentsCount = commentsCount + 1 WHERE id = :id")
    suspend fun incrementCommentsCount(id: Long)
}
