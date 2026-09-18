package com.example.util

import android.content.Context
import androidx.annotation.DrawableRes
import com.example.R

object ResourceHelper {
    @DrawableRes
    fun getDrawableRes(context: Context, resName: String): Int {
        return when (resName.lowercase()) {
            "thumb_tech_review" -> R.drawable.thumb_tech_review
            "photo_travel" -> R.drawable.photo_travel
            "short_dance" -> R.drawable.short_dance
            "channel_banner" -> R.drawable.channel_banner
            "ic_launcher_comotube" -> R.drawable.ic_launcher_comotube
            else -> {
                val id = context.resources.getIdentifier(resName, "drawable", context.packageName)
                if (id != 0) id else R.drawable.ic_launcher_comotube
            }
        }
    }
}
