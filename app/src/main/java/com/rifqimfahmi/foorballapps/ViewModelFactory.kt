package com.rifqimfahmi.foorballapps

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rifqimfahmi.foorballapps.data.source.SportRepository
import com.rifqimfahmi.foorballapps.data.source.local.SportDb
import com.rifqimfahmi.foorballapps.data.source.remote.SportServiceFactory
import com.rifqimfahmi.foorballapps.features.matchdetail.MatchViewModel
import com.rifqimfahmi.foorballapps.features.matches.MatchesViewModel
import com.rifqimfahmi.foorballapps.features.playerdetail.PlayerViewModel
import com.rifqimfahmi.foorballapps.features.searchmatch.SearchMatchViewModel
import com.rifqimfahmi.foorballapps.features.searchteam.SearchTeamViewModel
import com.rifqimfahmi.foorballapps.features.teamdetail.TeamViewModel


/**
 * фабрика для создания вьюмодели
 * @param application класс приложения,который инициализируется при старте и имеет глобальную видимость по всему приложению
 * @param sportRepository обьект репозитория для загрузки данных во вьюмодель
 *
 *
 */
class ViewModelFactory private constructor(
    private val application: Application,
    private val sportRepository: SportRepository
) : ViewModelProvider.NewInstanceFactory() {
    //основной метод фабрики,получает на вход класс вьюмодели,которую надо создать,создает вьюмодель.Создаваемая модель получает для своей
    // работы класс Application и репозиторий
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        with(modelClass) {
            when {
                isAssignableFrom(MatchesViewModel::class.java) ->
                    MatchesViewModel(application, sportRepository)
                isAssignableFrom(MatchViewModel::class.java) ->
                    MatchViewModel(application, sportRepository)
                isAssignableFrom(TeamViewModel::class.java) ->
                    TeamViewModel(application, sportRepository)
                isAssignableFrom(PlayerViewModel::class.java) ->
                    PlayerViewModel(application, sportRepository)
                isAssignableFrom(SearchMatchViewModel::class.java) ->
                    SearchMatchViewModel(application, sportRepository)
                isAssignableFrom(SearchTeamViewModel::class.java) ->
                    SearchTeamViewModel(application, sportRepository)
                else ->
                    error("Invalid View Model class")
            }
        } as T

    //создание фабрики
    companion object {
        //потокобезопастность.Если кто-то уже создает ViewModelFactory, то другие должны ждать когда она создастся.Одновременно нельзя создавать более
        // одного обьекта ViewModelFactory
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        fun getInstance(application: Application): ViewModelFactory {
            //если INSTANCE не равен null, то выполнятеся синхронизированный блок кода в котором происходит создание ViewModelFactory
            // Если провести аналогию с комнатами, то @Synchronized будет являться замком на двери, который открывается единственным
            // существующим ключом. Один человек (поток) берёт ключ, заходит, закрывается изнутри. Никто другой не может зайти в этот момент.
            // Только после того, как ключ вернут на место, это станет возможным. Именно так работает synchronized в Java и Kotlin.
            return INSTANCE ?: synchronized(ViewModelFactory::class.java) {
                ViewModelFactory(
                    application,
                    //создается репозиторий
                    SportRepository.getInstance(
                        SportDb.getDatabase(application.applicationContext),
                        SportServiceFactory.getService()
                    )
                //also это метод для обмена значениями между двумя переменными без участия третьей переменной.
                ).also { INSTANCE = it }
            }
        }
    }
}