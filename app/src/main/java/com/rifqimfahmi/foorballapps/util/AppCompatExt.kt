package com.rifqimfahmi.foorballapps.util

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.rifqimfahmi.foorballapps.ViewModelFactory


/** функция возвращающая сконструированную вьюмодель
 * @param viewModelClass параметризованный тип вьюмодели по которому нужно создать готовую вьюмодель
 * @param viewModelFactory фабрика для создания вьюмодели
 * @return T параметризованный тип вьюмодели
 */
fun <T : ViewModel> AppCompatActivity.obtainViewModel(
    viewModelClass: Class<T>,
    viewModelFactory: ViewModelProvider.Factory? = null
) : T {
    return if (viewModelFactory == null) {
        //если фабрика еще не была создана ранее,тогда создать,предоставив Application
        ViewModelProviders.of(this, ViewModelFactory.getInstance(application)).get(viewModelClass)
    } else {
        //если фабрика уже была создана ранее
        ViewModelProviders.of(this, viewModelFactory).get(viewModelClass)
    }
}