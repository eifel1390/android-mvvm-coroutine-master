package com.rifqimfahmi.foorballapps.features.matches

import android.app.SearchManager
import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import com.rifqimfahmi.foorballapps.R
import com.rifqimfahmi.foorballapps.features.matches.adapter.MatchesPagerAdapter
import com.rifqimfahmi.foorballapps.features.searchmatch.SearchMatchActivity
import kotlinx.android.synthetic.main.fragment_matches.*
import kotlinx.android.synthetic.main.fragment_matches.view.*

/**
 * фрагмент,отображает внутри себя две вкладки, next (будущие матчи) и last (прошедшие матчи) , а также выпадающий список для выбора лиг
 * установлен слушатель на нажатие на выпадающий список
 */

class MatchesFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private lateinit var viewModel: MatchesViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_matches, container, false).also {
            //инициализация вьюмодели MatchesViewModel
            viewModel = (activity as MatchesActivity).obtainViewModel()
            //слушатель на выпадающий список
            it.sp_leagues.onItemSelectedListener = this
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
        setupViewPager()
    }

    private fun setupViewPager() {
        //установка адаптера на прослушивание клика на табах next и last
        vpMatches.adapter = MatchesPagerAdapter(childFragmentManager)
        tabMatches.setupWithViewPager(vpMatches)
    }


    override fun onNothingSelected(parent: AdapterView<*>?) { }

    //вызывается если произошел выбор в выпадающем списке лиг
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        viewModel.setMatchesFilterBy(position)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.main_menu, menu)

        val searchManager = context?.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (menu?.findItem(R.id.menu_search)?.actionView as SearchView).apply {
            setSearchableInfo(searchManager.getSearchableInfo(ComponentName(context, SearchMatchActivity::class.java)))
        }
    }

    //статика для возврата инстанса фрагмента
    companion object {
        fun newInstance() = MatchesFragment()
    }
}