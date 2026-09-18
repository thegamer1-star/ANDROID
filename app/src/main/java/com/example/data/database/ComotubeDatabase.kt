package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.CommentDao
import com.example.data.dao.MediaDao
import com.example.data.dao.StoryDao
import com.example.data.entity.CommentEntity
import com.example.data.entity.MediaEntity
import com.example.data.entity.StoryEntity
import kotlinx.coroutines.CoroutineScope

@Database(
    entities = [MediaEntity::class, CommentEntity::class, StoryEntity::class],
    version = 2,
    exportSchema = false
)
abstract class ComotubeDatabase : RoomDatabase() {
    abstract fun mediaDao(): MediaDao
    abstract fun commentDao(): CommentDao
    abstract fun storyDao(): StoryDao

    companion object {
        @Volatile
        private var INSTANCE: ComotubeDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): ComotubeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ComotubeDatabase::class.java,
                    "comotube_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
