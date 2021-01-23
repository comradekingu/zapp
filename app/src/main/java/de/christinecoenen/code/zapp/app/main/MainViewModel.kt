package de.christinecoenen.code.zapp.app.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import de.christinecoenen.code.zapp.R
import de.christinecoenen.code.zapp.app.mediathek.repository.MediathekSearchSuggestionsProvider


class MainViewModel(
	application: Application,
	private val mediathekSearchSuggestionsProvider: MediathekSearchSuggestionsProvider
) : AndroidViewModel(application) {

	val pageCount get() = PageType.values().size

	private val searchQuerySource = MutableLiveData<String?>()
	var searchQuery: LiveData<String?> = searchQuerySource

	init {
		PreferenceManager.setDefaultValues(application, R.xml.preferences, false)
	}

	fun submitSearchQuery(query: String?) {
		searchQuerySource.postValue(query)

		if (query != null) {
			mediathekSearchSuggestionsProvider.saveQuery(query)
		}
	}

	fun getPageTypeAt(position: Int) = PageType.values()[position]

	fun getPageTypeFromMenuResId(itemId: Int) =
		when (itemId) {
			R.id.menu_live -> PageType.PAGE_CHANNEL_LIST
			R.id.menu_mediathek -> PageType.PAGE_MEDIATHEK_LIST
			R.id.menu_downloads -> PageType.PAGE_DOWNLOADS
			else -> throw IllegalArgumentException("Unknown menu item $itemId.")
		}
}
