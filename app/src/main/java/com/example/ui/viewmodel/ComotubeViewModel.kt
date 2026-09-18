package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.ComotubeDatabase
import com.example.data.entity.CommentEntity
import com.example.data.entity.MediaEntity
import com.example.data.entity.StoryEntity
import com.example.data.repository.ComotubeRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class BottomNavTab {
    HOME,
    SHORTS,
    SUBSCRIPTIONS,
    PROFILE
}

class ComotubeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ComotubeRepository

    init {
        val database = ComotubeDatabase.getDatabase(application, viewModelScope)
        repository = ComotubeRepository(
            database.mediaDao(),
            database.commentDao(),
            database.storyDao()
        )
    }

    // Navigation and UI Overlays
    private val _currentTab = MutableStateFlow(BottomNavTab.HOME)
    val currentTab: StateFlow<BottomNavTab> = _currentTab.asStateFlow()

    private val _activeVideo = MutableStateFlow<MediaEntity?>(null)
    val activeVideo: StateFlow<MediaEntity?> = _activeVideo.asStateFlow()

    private val _activeStory = MutableStateFlow<StoryEntity?>(null)
    val activeStory: StateFlow<StoryEntity?> = _activeStory.asStateFlow()

    private val _commentMedia = MutableStateFlow<MediaEntity?>(null)
    val commentMedia: StateFlow<MediaEntity?> = _commentMedia.asStateFlow()

    private val _showCreateDialog = MutableStateFlow(false)
    val showCreateDialog: StateFlow<Boolean> = _showCreateDialog.asStateFlow()

    // Filters
    private val _feedType = MutableStateFlow("ALL") // "ALL", "VIDEOS", "POSTS"
    val feedType: StateFlow<String> = _feedType.asStateFlow()

    private val _categoryFilter = MutableStateFlow("All")
    val categoryFilter: StateFlow<String> = _categoryFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Data streams
    val stories: StateFlow<List<StoryEntity>> = repository.allStories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val shortsList: StateFlow<List<MediaEntity>> = repository.getMediaByType("SHORT")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val subscribedMedia: StateFlow<List<MediaEntity>> = repository.subscribedMedia
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedMedia: StateFlow<List<MediaEntity>> = repository.savedMedia
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMedia: StateFlow<List<MediaEntity>> = repository.allMedia
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Combined filtered feed
    val feedMedia: StateFlow<List<MediaEntity>> = combine(
        repository.allMedia,
        _feedType,
        _categoryFilter,
        _searchQuery
    ) { allItems, type, cat, query ->
        allItems.filter { item ->
            val matchesType = when (type) {
                "VIDEOS" -> item.type == "VIDEO"
                "POSTS" -> item.type == "POST"
                else -> true // Blended: videos, posts, and shorts previews
            }
            val matchesCat = if (cat == "All") true else item.category.equals(cat, ignoreCase = true)
            val matchesQuery = if (query.isBlank()) true else {
                item.title.contains(query, ignoreCase = true) ||
                        item.caption.contains(query, ignoreCase = true) ||
                        item.authorName.contains(query, ignoreCase = true) ||
                        item.tags.contains(query, ignoreCase = true)
            }
            matchesType && matchesCat && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Dynamic comments for currently open comment sheet
    @OptIn(ExperimentalCoroutinesApi::class)
    val activeComments: StateFlow<List<CommentEntity>> = _commentMedia
        .flatMapLatest { media ->
            if (media == null) {
                kotlinx.coroutines.flow.flowOf(emptyList())
            } else {
                repository.getComments(media.id)
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Actions
    fun setTab(tab: BottomNavTab) {
        _currentTab.value = tab
    }

    fun setFeedType(type: String) {
        _feedType.value = type
    }

    fun setCategory(category: String) {
        _categoryFilter.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun openVideo(media: MediaEntity) {
        _activeVideo.value = media
        viewModelScope.launch {
            repository.incrementViews(media.id)
        }
    }

    fun closeVideo() {
        _activeVideo.value = null
    }

    fun openStory(story: StoryEntity) {
        _activeStory.value = story
        viewModelScope.launch {
            repository.markStoryViewed(story.id)
        }
    }

    fun closeStory() {
        _activeStory.value = null
    }

    fun openComments(media: MediaEntity) {
        _commentMedia.value = media
    }

    fun closeComments() {
        _commentMedia.value = null
    }

    fun openCreateDialog() {
        _showCreateDialog.value = true
    }

    fun closeCreateDialog() {
        _showCreateDialog.value = false
    }

    fun toggleLike(media: MediaEntity) {
        viewModelScope.launch {
            repository.toggleLike(media)
            // If active video is the one liked, update it
            if (_activeVideo.value?.id == media.id) {
                val newLiked = !media.isLiked
                val newLikes = if (newLiked) media.likesCount + 1 else (media.likesCount - 1).coerceAtLeast(0)
                _activeVideo.value = _activeVideo.value?.copy(isLiked = newLiked, likesCount = newLikes)
            }
        }
    }

    fun toggleDislike(media: MediaEntity) {
        viewModelScope.launch {
            repository.toggleDislike(media)
            if (_activeVideo.value?.id == media.id) {
                val newDisliked = !media.isDisliked
                val newDislikes = if (newDisliked) media.dislikesCount + 1 else (media.dislikesCount - 1).coerceAtLeast(0)
                _activeVideo.value = _activeVideo.value?.copy(isDisliked = newDisliked, dislikesCount = newDislikes)
            }
        }
    }

    fun toggleSave(media: MediaEntity) {
        viewModelScope.launch {
            repository.toggleSave(media)
            if (_activeVideo.value?.id == media.id) {
                _activeVideo.value = _activeVideo.value?.copy(isSaved = !media.isSaved)
            }
        }
    }

    fun toggleSubscribe(authorHandle: String, currentSubscribed: Boolean) {
        viewModelScope.launch {
            repository.toggleSubscribe(authorHandle, currentSubscribed)
            if (_activeVideo.value?.authorHandle == authorHandle) {
                _activeVideo.value = _activeVideo.value?.copy(isSubscribed = !currentSubscribed)
            }
        }
    }

    fun addComment(mediaId: Long, text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            repository.addComment(mediaId, "You (Comotuber)", text)
        }
    }

    fun toggleCommentLike(comment: CommentEntity) {
        viewModelScope.launch {
            repository.toggleCommentLike(comment)
        }
    }

    fun createMedia(
        type: String, // "POST", "VIDEO", "SHORT"
        title: String,
        caption: String,
        category: String,
        tags: String,
        duration: String = "",
        mediaRes: String = ""
    ) {
        viewModelScope.launch {
            val resolvedRes = if (mediaRes.isNotBlank()) {
                mediaRes
            } else {
                when (type) {
                    "VIDEO" -> "thumb_tech_review"
                    "SHORT" -> "short_dance"
                    else -> "photo_travel"
                }
            }
            val newMedia = MediaEntity(
                type = type,
                title = if (title.isBlank()) "New $type by You" else title,
                authorName = "You (Comotuber)",
                authorHandle = "@you_official",
                authorAvatarRes = "ic_launcher_comotube",
                mediaRes = resolvedRes,
                duration = if (duration.isBlank()) (if (type == "VIDEO") "12:30" else if (type == "SHORT") "0:30" else "") else duration,
                viewsCount = 1,
                likesCount = 0,
                dislikesCount = 0,
                commentsCount = 0,
                caption = caption,
                tags = if (tags.isBlank()) "#comotube #creator" else tags,
                audioTrack = "Original Audio - You",
                category = category,
                isLiked = false,
                isDisliked = false,
                isSaved = false,
                isSubscribed = false,
                timestamp = System.currentTimeMillis()
            )
            repository.insertMedia(newMedia)
            closeCreateDialog()
        }
    }

    fun addStory(caption: String) {
        viewModelScope.launch {
            val newStory = StoryEntity(
                authorName = "Your Story",
                authorHandle = "@you_official",
                authorAvatarRes = "ic_launcher_comotube",
                storyMediaRes = "photo_travel",
                caption = caption.ifBlank { "Sharing a moment on Comotube! ✨" },
                isViewed = false,
                timestamp = System.currentTimeMillis()
            )
            repository.addStory(newStory)
            closeCreateDialog()
        }
    }
}
