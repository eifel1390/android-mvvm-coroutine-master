package com.rifqimfahmi.foorballapps.features.matches.adapter

import android.animation.AnimatorInflater
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.CalendarContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rifqimfahmi.foorballapps.R
import com.rifqimfahmi.foorballapps.features.base.BaseRVAdapter
import com.rifqimfahmi.foorballapps.features.matches.MatchesListFragment
import com.rifqimfahmi.foorballapps.vo.Match
import com.rifqimfahmi.foorballapps.vo.Resource
import kotlinx.android.synthetic.main.item_match.view.*

/**
 * адаптер для списка матчей NEXT или LAST
 */

class MatchesAdapter(ctx: Context?, resource: Resource<List<Match>>, private val clickListener: (Match) -> Unit) :
    BaseRVAdapter<Match>(ctx, resource) {

    override var errorMessage = "Failed to load events"

    override fun createDataViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return MatchItem(LayoutInflater.from(parent.context).inflate(R.layout.item_match, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MatchItem) {
            holder.bind(resource.data?.get(position), clickListener)
        }
    }

    //наполнение каждого итема данными из каждого матча
    inner class MatchItem(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(nMatch: Match?, clickListener: (Match) -> Unit) {
            nMatch?.let { match ->
                //Функция with позволяет выполнить несколько операций над одним объектом, не повторяя его имени.
                with(itemView) {
                    tv_hour.text = match.getHour()
                    tv_date.text = match.getDate()
                    tv_club1.text = match.strHomeTeam
                    tv_score1.text = match.intHomeScore
                    tv_club2.text = match.strAwayTeam
                    tv_score2.text = match.intAwayScore
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        itemView.stateListAnimator =
                                AnimatorInflater.loadStateListAnimator(this.context, R.animator.lift_on_touch)
                    }
                    //если у матча стоит метка что это следующий матч,то отображается знак что можно установить напоминание
                    if (match.matchType == MatchesListFragment.TYPE_NEXT_MATCH) {
                        iv_notification.visibility = View.VISIBLE
                        //слушатель нажатия по знаку установки напоминания.Устанавливается напоминание в гугл календаре
                        iv_notification.setOnClickListener {
                            ctx?.startActivity(Intent(Intent.ACTION_INSERT).apply {
                                data = CalendarContract.Events.CONTENT_URI
                                putExtra(CalendarContract.Events.TITLE, "${match.strHomeTeam} vs ${match.strAwayTeam}")
                                putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, match.getStartTime())
                            })
                        }
                    }
                    setOnClickListener {
                        clickListener(match)
                    }
                }
            }
        }
    }
}
