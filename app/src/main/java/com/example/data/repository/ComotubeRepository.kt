package com.example.data.repository

import com.example.data.dao.CommentDao
import com.example.data.dao.MediaDao
import com.example.data.dao.StoryDao
import com.example.data.entity.CommentEntity
import com.example.data.entity.MediaEntity
import com.example.data.entity.StoryEntity
import kotlinx.coroutines.flow.Flow

class ComotubeRepository(
    private val mediaDao: MediaDao,
    private val commentDao: CommentDao,
    private val storyDao: StoryDao
) {
    val allMedia: Flow<List<MediaEntity>> = mediaDao.getAllMedia()
    val allStories: Flow<List<StoryEntity>> = storyDao.getAllStories()
    val subscribedMedia: Flow<List<MediaEntity>> = mediaDao.getSubscribedMedia()
    val savedMedia: Flow<List<MediaEntity>> = mediaDao.getSavedMedia()

    fun getMediaByType(type: String): Flow<List<MediaEntity>> = mediaDao.getMediaByType(type)

    fun getMediaByCategory(category: String): Flow<List<MediaEntity>> {
        return if (category.equals("All", ignoreCase = true)) {
            mediaDao.getAllMedia()
        } else {
            mediaDao.getMediaByCategory(category)
        }
    }

    fun getMediaById(id: Long): Flow<MediaEntity?> = mediaDao.getMediaById(id)

    fun searchMedia(query: String): Flow<List<MediaEntity>> = mediaDao.searchMedia(query)

    fun getComments(mediaId: Long): Flow<List<CommentEntity>> = commentDao.getCommentsForMedia(mediaId)

    suspend fun insertMedia(media: MediaEntity): Long = mediaDao.insertMedia(media)

    suspend fun toggleLike(media: MediaEntity) {
        val newIsLiked = !media.isLiked
        val newLikes = if (newIsLiked) media.likesCount + 1 else (media.likesCount - 1).coerceAtLeast(0)
        mediaDao.updateLike(media.id, newIsLiked, newLikes)

        // If liking, clear dislike if set
        if (newIsLiked && media.isDisliked) {
            val newDislikes = (media.dislikesCount - 1).coerceAtLeast(0)
            mediaDao.updateDislike(media.id, false, newDislikes)
        }
    }

    suspend fun toggleDislike(media: MediaEntity) {
        val newIsDisliked = !media.isDisliked
        val newDislikes = if (newIsDisliked) media.dislikesCount + 1 else (media.dislikesCount - 1).coerceAtLeast(0)
        mediaDao.updateDislike(media.id, newIsDisliked, newDislikes)

        // If disliking, clear like if set
        if (newIsDisliked && media.isLiked) {
            val newLikes = (media.likesCount - 1).coerceAtLeast(0)
            mediaDao.updateLike(media.id, false, newLikes)
        }
    }

    suspend fun toggleSave(media: MediaEntity) {
        val newSaved = !media.isSaved
        mediaDao.updateSave(media.id, newSaved)
    }

    suspend fun toggleSubscribe(authorHandle: String, currentSubscribed: Boolean) {
        mediaDao.updateSubscriptionByAuthor(authorHandle, !currentSubscribed)
    }

    suspend fun incrementViews(mediaId: Long) {
        mediaDao.incrementViews(mediaId)
    }

    suspend fun addComment(mediaId: Long, authorName: String, text: String): Long {
        val comment = CommentEntity(
            mediaId = mediaId,
            authorName = authorName,
            authorAvatarRes = "ic_launcher_comotube",
            text = text,
            likesCount = 0,
            isLiked = false,
            timestamp = System.currentTimeMillis()
        )
        val id = commentDao.insertComment(comment)
        mediaDao.incrementCommentsCount(mediaId)
        return id
    }

    suspend fun toggleCommentLike(comment: CommentEntity) {
        val newIsLiked = !comment.isLiked
        val newLikes = if (newIsLiked) comment.likesCount + 1 else (comment.likesCount - 1).coerceAtLeast(0)
        commentDao.updateCommentLike(comment.id, newIsLiked, newLikes)
    }

    suspend fun addStory(story: StoryEntity): Long = storyDao.insertStory(story)

    suspend fun markStoryViewed(storyId: Long) {
        storyDao.markStoryViewed(storyId)
    }
}
