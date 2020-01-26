package com.rifqimfahmi.foorballapps.features.matches

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.rifqimfahmi.foorballapps.R
import com.rifqimfahmi.foorballapps.features.matchdetail.MatchDetailActivity
import com.rifqimfahmi.foorballapps.features.matches.adapter.MatchesAdapter
import com.rifqimfahmi.foorballapps.vo.Match
import com.rifqimfahmi.foorballapps.vo.Resource
import com.rifqimfahmi.foorballapps.vo.Status
import kotlinx.android.synthetic.main.list_items.*

/**
 * фрагмент для показа списка NEXT или LAST матчей
 */

class MatchesListFragment : Fragment() {

    private lateinit var viewModel: MatchesViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.list_items, container, false).also { view ->
            //инициализация вьюмодели
            viewModel = (activity as MatchesActivity).obtainViewModel()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupList()
    }

    private fun setupList() {
        //установка слушателя для обновления списка матчей по свайпу вниз
        srl_list.setOnRefreshListener { viewModel.refreshMatches() }

        rv_list.layoutManager = LinearLayoutManager(context)
        //установка адаптера для recyclerView со списком матчей, адаптеру передается контекст, обертка Resource и реализация слушателя нажатия по матчу
        rv_list.adapter = MatchesAdapter(context, Resource.loading(null)) {
            context?.startActivity(
                MatchDetailActivity.getStartIntent(context, it.idEvent, it.idHomeTeam, it.idAwayTeam)
            )
        }
            //проверка константы с которой вызвали фрагмент
        when (getType()) {
            TYPE_NEXT_MATCH -> {
                //запрос у вьюмодели набора следующих матчей,установка подписчика на этот набор
                viewModel.nextMatches.observe(activity as MatchesActivity, Observer { data ->
                    updateData(data)
                })
            }
            TYPE_PREV_MATCH -> {
                viewModel.prevMatch.observe(activity as MatchesActivity, Observer { data ->
                    updateData(data)
                })
            }
        }
    }

    //вызывается когда из вьюмодели пришел набор матчей, обновление списка
    private fun updateData(data: Resource<List<Match>>?) {
        if (data == null || rv_list == null) return
        (rv_list.adapter as MatchesAdapter).submitData(data)
        updateRefreshIndicator(data)
    }

    private fun <T> updateRefreshIndicator(data: Resource<List<T>>) {
        srl_list.isRefreshing = data.status == Status.LOADING
    }

    private fun getType(): String? {
        return arguments?.getString(KEY_MATCH)
    }

    //статика для возврата инстанса MatchesListFragment
    companion object {

        private const val KEY_MATCH = "key_match"
        const val TYPE_NEXT_MATCH = "type_next_match"
        const val TYPE_PREV_MATCH = "type_prev_match"

        fun newInstance(type: String): MatchesListFragment {
            val fragment = MatchesListFragment()
                //в аргумент кладется константа с которой вызвали фрагмент
            fragment.arguments = Bundle().apply {
                putString(KEY_MATCH, type)
            }
            return fragment
        }
    }
}