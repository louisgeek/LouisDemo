package com.louis.mycomposeproject.util

import java.text.SimpleDateFormat
import java.util.*

object DateTimeUtils {
    private const val DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss"
    private const val DATE_FORMAT = "yyyy-MM-dd"
    private const val TIME_FORMAT = "HH:mm:ss"
    
    fun getCurrentTimestamp(): Long = System.currentTimeMillis()
    
    fun formatTimestamp(timestamp: Long, pattern: String = DEFAULT_FORMAT): String {
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
    
    fun formatDate(timestamp: Long): String = formatTimestamp(timestamp, DATE_FORMAT)
    
    fun formatTime(timestamp: Long): String = formatTimestamp(timestamp, TIME_FORMAT)
    
    fun parseDate(dateString: String, pattern: String = DEFAULT_FORMAT): Long {
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        return sdf.parse(dateString)?.time ?: 0
    }
    
    fun isToday(timestamp: Long): Boolean {
        val today = Calendar.getInstance()
        val date = Calendar.getInstance().apply { timeInMillis = timestamp }
        return today.get(Calendar.YEAR) == date.get(Calendar.YEAR) &&
                today.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR)
    }
    
    fun getRelativeTime(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        
        return when {
            diff < 60_000 -> "刚刚"
            diff < 3600_000 -> "${diff / 60_000}分钟前"
            diff < 86400_000 -> "${diff / 3600_000}小时前"
            diff < 604800_000 -> "${diff / 86400_000}天前"
            else -> formatDate(timestamp)
        }
    }
}
