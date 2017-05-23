/*
 * NewsCatchr
 * Copyright © 2017 Jan-Lukas Else
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package jlelse.newscatchr.extensions

import android.content.Context
import com.afollestad.materialdialogs.MaterialDialog
import jlelse.newscatchr.backend.Feed
import jlelse.newscatchr.backend.apis.Feedly
import jlelse.newscatchr.mainAcivity
import jlelse.newscatchr.ui.fragments.FeedListView
import jlelse.newscatchr.ui.views.ProgressDialog
import jlelse.readit.R
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

fun searchForFeeds(context: Context, query: String? = null) {
	val progressDialog = ProgressDialog(context)
	val load = { finalQuery: String ->
		progressDialog.show()
		context.doAsync {
			var foundFeeds: Array<Feed>? = null
			var foundRelated: Array<String>? = null
			Feedly().feedSearch(finalQuery, 100, null, null) { feeds, related ->
				foundFeeds = feeds
				foundRelated = related
			}
			uiThread {
				progressDialog.dismiss()
				if (foundFeeds.notNullAndEmpty()) mainAcivity?.openView(FeedListView(feeds = foundFeeds, tags = foundRelated).apply { title = "${R.string.search_results_for.resStr()} $finalQuery" })
				else context.nothingFound()
			}
		}
	}
	if (query.isNullOrBlank()) {
		MaterialDialog.Builder(context)
				.title(android.R.string.search_go)
				.input(null, null) { _, input ->
					load(input.toString())
				}
				.negativeText(android.R.string.cancel)
				.positiveText(android.R.string.search_go)
				.show()
	} else {
		load(query!!)
	}
}