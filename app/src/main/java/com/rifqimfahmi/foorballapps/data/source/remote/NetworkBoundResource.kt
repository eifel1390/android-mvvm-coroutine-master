package com.rifqimfahmi.foorballapps.data.source.remote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.rifqimfahmi.foorballapps.ContextProviders
import com.rifqimfahmi.foorballapps.vo.Resource
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * общий параметризованный класс для всех типов данных,запрашиваемых из репозитория
 */

abstract class NetworkBoundResource<ResultType, RequestType>
constructor(private val contextProviders: ContextProviders) {

    private val result = MediatorLiveData<Resource<ResultType>>()

    //вызывается при создании класса
    init {
        //в результирующую livedata кладется null
        result.value = Resource.loading(null)
        //идет загрузка данных из БД
        val dbSource = loadFromDb()
        //в результирующую livedata записываем данные из бд (dbSource)
        result.addSource(dbSource) { data ->
            //отписываемся от dbSource, он больше не нужен
            result.removeSource(dbSource)
            //проверка новых данных с сервера
            if (shouldFetch(data)) {
                //отправка данных с бдна сервер для проверки свежие ли они
                fetchFromNetwork(dbSource)
            } else {
                result.addSource(dbSource) { newData ->
                    setValue(Resource.success(newData))
                }
            }
        }
    }

    private fun setValue(newValue: Resource<ResultType>) {
        if (result.value != newValue) {
            result.value = newValue
        }
    }

    //метод делает запрос данных с сервера.Принимает на вход livedata result,в которую ранее уже положены данные из бд
    private fun fetchFromNetwork(dbSource: LiveData<ResultType>) {
        //создается apiResponse
        val apiResponse = createCall()
        //в livedata result
        result.addSource(dbSource) { newData ->
            setValue(Resource.loading(newData))
        }


        result.addSource(apiResponse) { response ->
            result.removeSource(apiResponse)
            result.removeSource(dbSource)
            when (response) {
                is ApiSuccessResponse -> {
                    GlobalScope.launch(contextProviders.IO) {
                        saveCallResult(processResponse(response))
                        GlobalScope.launch(contextProviders.Main) {
                            result.addSource(loadFromDb()) { newData ->
                                setValue(Resource.success(newData))
                            }
                        }
                    }
                }
                is ApiEmptyResponse -> {
                    GlobalScope.launch(contextProviders.Main) {
                        result.addSource(loadFromDb()) { newData ->
                            setValue(Resource.success(newData))
                        }
                    }
                }
                is ApiErrorResponse -> {
                    onFetchFailed()
                    result.addSource(dbSource) { newData ->
                        setValue(Resource.error(response.errorMessage, newData))
                    }
                }
            }
        }
    }

    protected open fun onFetchFailed() {}

    fun asLiveData() = result as LiveData<Resource<ResultType>>

    abstract fun saveCallResult(item: RequestType)

    protected fun processResponse(response: ApiSuccessResponse<RequestType>) = response.body

    abstract fun createCall(): LiveData<ApiResponse<RequestType>>

    protected abstract fun shouldFetch(data: ResultType?): Boolean

    protected abstract fun loadFromDb(): LiveData<ResultType>

}