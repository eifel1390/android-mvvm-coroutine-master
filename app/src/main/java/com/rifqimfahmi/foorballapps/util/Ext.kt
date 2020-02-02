package com.rifqimfahmi.foorballapps.util

import android.content.Context
import android.util.Log
import com.rifqimfahmi.foorballapps.R

/**
 * кастомные расширения для контекста
 */
 
fun Context.getLeaguesName(position: Int) : String {
    return resources.getStringArray(R.array.leagues)[position]
}

//принимает ид лиги выбранной в выпадающем списке, конвертит ее в стрингу c ид.Массив со стрингами хранится в strings - ресурсе
fun Context.getLeaguesId(position: Int) : String {
    return resources.getStringArray(R.array.leagues_id)[position]
}