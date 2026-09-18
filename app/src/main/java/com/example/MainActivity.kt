package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Subscriptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.CommentsBottomSheet
import com.example.ui.components.StoryViewerDialog
import com.example.ui.screens.CreateMediaDialog
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ShortsReelsScreen
import com.example.ui.screens.SubscriptionsScreen
import com.example.ui.screens.VideoPlayerScreen
import com.example.ui.theme.ComoBrandGradient
import com.example.ui.theme.ComoCrimson
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.BottomNavTab
import com.example.ui.viewmodel.ComotubeViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: ComotubeViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                ComotubeApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComotubeApp(viewModel: ComotubeViewModel) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val activeVideo by viewModel.activeVideo.collectAsStateWithLifecycle()
    val activeStory by viewModel.activeStory.collectAsStateWithLifecycle()
    val commentMedia by viewModel.commentMedia.collectAsStateWithLifecycle()
    val showCreateDialog by viewModel.showCreateDialog.collectAsStateWithLifecycle()

    val feedMedia by viewModel.feedMedia.collectAsStateWithLifecycle()
    val stories by viewModel.stories.collectAsStateWithLifecycle()
    val shortsList by viewModel.shortsList.collectAsStateWithLifecycle()
    val subscribedMedia by viewModel.subscribedMedia.collectAsStateWithLifecycle()
    val savedMedia by viewModel.savedMedia.collectAsStateWithLifecycle()
    val allMedia by viewModel.allMedia.collectAsStateWithLifecycle()

    val feedType by viewModel.feedType.collectAsStateWithLifecycle()
    val categoryFilter by viewModel.categoryFilter.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val activeComments by viewModel.activeComments.collectAsStateWithLifecycle()

    val commentsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val createSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Handle back button when video or overlays are active
    BackHandler(enabled = activeVideo != null || activeStory != null || commentMedia != null || showCreateDialog) {
        when {
            activeVideo != null -> viewModel.closeVideo()
            activeStory != null -> viewModel.closeStory()
            commentMedia != null -> viewModel.closeComments()
            showCreateDialog -> viewModel.closeCreateDialog()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            // Hide bottom bar when viewing active video or story
            if (activeVideo == null && activeStory == null) {
                ComotubeBottomNavBar(
                    currentTab = currentTab,
                    onTabSelected = { tab -> viewModel.setTab(tab) },
                    onCreateClick = { viewModel.openCreateDialog() }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "tab_navigation"
            ) { tab ->
                when (tab) {
                    BottomNavTab.HOME -> {
                        HomeScreen(
                            stories = stories,
                            feedMedia = feedMedia,
                            selectedFeedType = feedType,
                            selectedCategory = categoryFilter,
                            searchQuery = searchQuery,
                            onFeedTypeChange = { viewModel.setFeedType(it) },
                            onCategoryChange = { viewModel.setCategory(it) },
                            onSearchQueryChange = { viewModel.setSearchQuery(it) },
                            onStoryClick = { viewModel.openStory(it) },
                            onAddStoryClick = { viewModel.openCreateDialog() },
                            onVideoClick = { viewModel.openVideo(it) },
                            onLikeClick = { viewModel.toggleLike(it) },
                            onCommentClick = { viewModel.openComments(it) },
                            onSaveClick = { viewModel.toggleSave(it) },
                            onSubscribeClick = { handle, current -> viewModel.toggleSubscribe(handle, current) },
                            onShareClick = { /* Share */ },
                            onUploadClick = { viewModel.openCreateDialog() }
                        )
                    }
                    BottomNavTab.SHORTS -> {
                        ShortsReelsScreen(
                            shorts = shortsList,
                            onLikeClick = { viewModel.toggleLike(it) },
                            onDislikeClick = { viewModel.toggleDislike(it) },
                            onSaveClick = { viewModel.toggleSave(it) },
                            onSubscribeClick = { handle, current -> viewModel.toggleSubscribe(handle, current) },
                            onCommentClick = { viewModel.openComments(it) },
                            onShareClick = { /* Share */ },
                            onCreateClick = { viewModel.openCreateDialog() }
                        )
                    }
                    BottomNavTab.SUBSCRIPTIONS -> {
                        SubscriptionsScreen(
                            subscribedMedia = subscribedMedia,
                            onVideoClick = { viewModel.openVideo(it) },
                            onLikeClick = { viewModel.toggleLike(it) },
                            onCommentClick = { viewModel.openComments(it) },
                            onSaveClick = { viewModel.toggleSave(it) },
                            onSubscribeClick = { handle, current -> viewModel.toggleSubscribe(handle, current) },
                            onShareClick = { /* Share */ },
                            onUploadClick = { viewModel.openCreateDialog() }
                        )
                    }
                    BottomNavTab.PROFILE -> {
                        ProfileScreen(
                            allMedia = allMedia,
                            savedMedia = savedMedia,
                            onVideoClick = { viewModel.openVideo(it) },
                            onSaveClick = { viewModel.toggleSave(it) },
                            onShareClick = { /* Share */ },
                            onUploadClick = { viewModel.openCreateDialog() }
                        )
                    }
                }
            }

            // Fullscreen YouTube Watch Player Screen Overlay
            activeVideo?.let { video ->
                VideoPlayerScreen(
                    video = video,
                    recommendedVideos = allMedia,
                    onBackClick = { viewModel.closeVideo() },
                    onLikeClick = { viewModel.toggleLike(video) },
                    onDislikeClick = { viewModel.toggleDislike(video) },
                    onSaveClick = { viewModel.toggleSave(video) },
                    onSubscribeClick = { viewModel.toggleSubscribe(video.authorHandle, video.isSubscribed) },
                    onShareClick = { /* Share */ },
                    onOpenComments = { viewModel.openComments(video) },
                    onSelectRecommendedVideo = { viewModel.openVideo(it) }
                )
            }

            // Fullscreen Story Viewer Overlay
            activeStory?.let { story ->
                StoryViewerDialog(
                    story = story,
                    onDismiss = { viewModel.closeStory() }
                )
            }

            // Comments Bottom Sheet
            commentMedia?.let { media ->
                CommentsBottomSheet(
                    media = media,
                    comments = activeComments,
                    onDismiss = { viewModel.closeComments() },
                    onAddComment = { text -> viewModel.addComment(media.id, text) },
                    onLikeComment = { comment -> viewModel.toggleCommentLike(comment) },
                    sheetState = commentsSheetState
                )
            }

            // Create Content Bottom Sheet Dialog
            if (showCreateDialog) {
                CreateMediaDialog(
                    onDismiss = { viewModel.closeCreateDialog() },
                    onCreateMedia = { type, title, caption, category, tags, duration, mediaRes ->
                        viewModel.createMedia(type, title, caption, category, tags, duration, mediaRes)
                    },
                    onAddStory = { caption ->
                        viewModel.addStory(caption)
                    },
                    sheetState = createSheetState
                )
            }
        }
    }
}

@Composable
fun ComotubeBottomNavBar(
    currentTab: BottomNavTab,
    onTabSelected: (BottomNavTab) -> Unit,
    onCreateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 6.dp,
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .testTag("bottom_nav_bar")
    ) {
        // Home (IG + YT blended feed)
        NavigationBarItem(
            selected = currentTab == BottomNavTab.HOME,
            onClick = { onTabSelected(BottomNavTab.HOME) },
            icon = {
                Icon(
                    imageVector = if (currentTab == BottomNavTab.HOME) Icons.Default.Home else Icons.Outlined.Home,
                    contentDescription = "Home"
                )
            },
            label = {
                Text(
                    text = "Home",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = if (currentTab == BottomNavTab.HOME) FontWeight.Bold else FontWeight.Normal
                    )
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = ComoCrimson,
                selectedTextColor = ComoCrimson,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            ),
            modifier = Modifier.testTag("nav_item_home")
        )

        // Shorts & Reels
        NavigationBarItem(
            selected = currentTab == BottomNavTab.SHORTS,
            onClick = { onTabSelected(BottomNavTab.SHORTS) },
            icon = {
                Icon(
                    imageVector = if (currentTab == BottomNavTab.SHORTS) Icons.Default.Movie else Icons.Outlined.Movie,
                    contentDescription = "Shorts & Reels"
                )
            },
            label = {
                Text(
                    text = "Shorts",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = if (currentTab == BottomNavTab.SHORTS) FontWeight.Bold else FontWeight.Normal
                    )
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = ComoCrimson,
                selectedTextColor = ComoCrimson,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            ),
            modifier = Modifier.testTag("nav_item_shorts")
        )

        // Center Create (+) Button
        Box(
            modifier = Modifier
                .weight(1f)
                .clickable(onClick = onCreateClick)
                .testTag("nav_item_create"),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(ComoBrandGradient)
                    .border(1.5.dp, Color.White.copy(alpha = 0.8f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Subscriptions
        NavigationBarItem(
            selected = currentTab == BottomNavTab.SUBSCRIPTIONS,
            onClick = { onTabSelected(BottomNavTab.SUBSCRIPTIONS) },
            icon = {
                Icon(
                    imageVector = if (currentTab == BottomNavTab.SUBSCRIPTIONS) Icons.Default.Subscriptions else Icons.Outlined.Subscriptions,
                    contentDescription = "Subscriptions"
                )
            },
            label = {
                Text(
                    text = "Subs",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = if (currentTab == BottomNavTab.SUBSCRIPTIONS) FontWeight.Bold else FontWeight.Normal
                    )
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = ComoCrimson,
                selectedTextColor = ComoCrimson,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            ),
            modifier = Modifier.testTag("nav_item_subs")
        )

        // Profile / You
        NavigationBarItem(
            selected = currentTab == BottomNavTab.PROFILE,
            onClick = { onTabSelected(BottomNavTab.PROFILE) },
            icon = {
                Icon(
                    imageVector = if (currentTab == BottomNavTab.PROFILE) Icons.Default.Person else Icons.Outlined.Person,
                    contentDescription = "Profile"
                )
            },
            label = {
                Text(
                    text = "You",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = if (currentTab == BottomNavTab.PROFILE) FontWeight.Bold else FontWeight.Normal
                    )
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = ComoCrimson,
                selectedTextColor = ComoCrimson,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            ),
            modifier = Modifier.testTag("nav_item_profile")
        )
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}
