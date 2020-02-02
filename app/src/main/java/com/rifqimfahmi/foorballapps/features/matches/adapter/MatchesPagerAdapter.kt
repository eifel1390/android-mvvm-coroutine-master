package com.rifqimfahmi.foorballapps.features.matches.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.rifqimfahmi.foorballapps.features.matches.MatchesListFragment

/*
 * адаптер для прослушивания  клика на табах next и last
 */
 
class MatchesPagerAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> MatchesListFragment.newInstance(MatchesListFragment.TYPE_NEXT_MATCH)
            1 -> MatchesListFragment.newInstance(MatchesListFragment.TYPE_PREV_MATCH)
            else -> error("Cannot create more than two fragment")
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "NEXT"
            1 -> "LAST"
            else -> super.getPageTitle(position)
        }
    }

    override fun getCount(): Int = 2

}