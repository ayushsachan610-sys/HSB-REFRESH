package com.hsbrefresh.fest.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateHelper {

    fun formatIsoTimestamp(isoString: String?): String {
        if (isoString.isNullOrEmpty()) return "—"
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val date = parser.parse(isoString.substringBefore("."))
            val formatter = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
            if (date != null) formatter.format(date) else isoString
        } catch (e: Exception) {
            isoString
        }
    }
}
