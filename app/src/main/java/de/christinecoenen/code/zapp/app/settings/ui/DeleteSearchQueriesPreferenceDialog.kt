package de.christinecoenen.code.zapp.app.settings.ui

import android.os.Bundle
import androidx.preference.PreferenceDialogFragmentCompat
import de.christinecoenen.code.zapp.app.mediathek.repository.MediathekSearchSuggestionsProvider
import org.koin.android.ext.android.inject

class DeleteSearchQueriesPreferenceDialog : PreferenceDialogFragmentCompat() {

	companion object {

		fun newInstance(key: String): DeleteSearchQueriesPreferenceDialog {
			return DeleteSearchQueriesPreferenceDialog().apply {
				arguments = Bundle().apply {
					putString(ARG_KEY, key)
				}
			}
		}

	}

	private val mediathekSearchSuggestionsProvider: MediathekSearchSuggestionsProvider by inject()

	override fun onDialogClosed(positiveResult: Boolean) {
		if (positiveResult) {
			mediathekSearchSuggestionsProvider.deleteAllQueries()
		}
	}
}
