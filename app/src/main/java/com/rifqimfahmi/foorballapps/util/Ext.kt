package com.rifqimfahmi.foorballapps.util

import android.content.Context
import com.rifqimfahmi.foorballapps.R

/**
 * кастомные расширения для контекста
 */
 
fun Context.getLeaguesName(position: Int) : String {
    return resources.getStringArray(R.array.leagues)[position]
}

//возвращает id лиги
fun Context.getLeaguesId(position: Int) : String {
    return resources.getStringArray(R.array.leagues_id)[position]
}