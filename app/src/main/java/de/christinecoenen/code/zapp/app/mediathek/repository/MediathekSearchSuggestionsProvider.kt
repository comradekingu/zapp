package de.christinecoenen.code.zapp.app.mediathek.repository

import android.content.Context
import android.content.SearchRecentSuggestionsProvider
import android.provider.SearchRecentSuggestions

class MediathekSearchSuggestionsProvider(
	private val applicationContext: Context? = null
) : SearchRecentSuggestionsProvider() {

	companion object {

		private const val AUTHORITY = "de.christinecoenen.code.zapp.MediathekSearchSuggestionsProvider"
		private const val MODE = DATABASE_MODE_QUERIES
	}

	@Suppress("unused")
	constructor() : this(null)

	init {
		setupSuggestions(AUTHORITY, MODE)
	}

	fun saveQuery(query: String) {
		val suggestions = SearchRecentSuggestions(applicationContext ?: context, AUTHORITY, MODE)
		suggestions.saveRecentQuery(query, null)
	}

	fun deleteAllQueries() {
		val suggestions = SearchRecentSuggestions(applicationContext ?: context, AUTHORITY, MODE)
		suggestions.clearHistory()
	}
}
