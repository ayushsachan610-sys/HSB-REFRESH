package com.hsbrefresh.fest.utils

import android.widget.TextView
import androidx.core.content.ContextCompat
import com.hsbrefresh.fest.R

object CategoryHelper {

    fun applyCategoryBadge(textView: TextView, category: String?) {
        val context = textView.context
        val cat = category?.lowercase() ?: ""

        when (cat) {
            "technical" -> {
                textView.setBackgroundResource(R.drawable.bg_badge_technical)
                textView.setTextColor(ContextCompat.getColor(context, R.color.cat_tech_text))
            }
            "cultural" -> {
                textView.setBackgroundResource(R.drawable.bg_badge_cultural)
                textView.setTextColor(ContextCompat.getColor(context, R.color.cat_cult_text))
            }
            "sports" -> {
                textView.setBackgroundResource(R.drawable.bg_badge_sports)
                textView.setTextColor(ContextCompat.getColor(context, R.color.cat_sports_text))
            }
            "gaming" -> {
                textView.setBackgroundResource(R.drawable.bg_badge_gaming)
                textView.setTextColor(ContextCompat.getColor(context, R.color.cat_gaming_text))
            }
            "workshop" -> {
                textView.setBackgroundResource(R.drawable.bg_badge_workshop)
                textView.setTextColor(ContextCompat.getColor(context, R.color.cat_work_text))
            }
            else -> {
                textView.setBackgroundResource(R.drawable.bg_badge_default)
                textView.setTextColor(ContextCompat.getColor(context, R.color.slate_300))
            }
        }
    }
}
