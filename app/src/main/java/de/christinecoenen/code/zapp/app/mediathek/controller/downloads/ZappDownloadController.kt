package de.christinecoenen.code.zapp.app.mediathek.controller.downloads

import de.christinecoenen.code.zapp.app.mediathek.controller.downloads.exceptions.DownloadException
import de.christinecoenen.code.zapp.models.shows.DownloadStatus
import de.christinecoenen.code.zapp.models.shows.PersistedMediathekShow
import de.christinecoenen.code.zapp.models.shows.Quality
import de.christinecoenen.code.zapp.repositories.MediathekRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.joda.time.DateTime


class ZappDownloadController(
	private val mediathekRepository: MediathekRepository,
	private val fileDownloader: FileDownloader,
	private val downloadFileInfoManager: DownloadFileInfoManager
) : IDownloadController {

	// TODO: use service or work manager for background support
	override suspend fun startDownload(show: PersistedMediathekShow, quality: Quality) =
		withContext(Dispatchers.IO) {
			val downloadUrl = show.mediathekShow.getVideoUrl(quality)
				?: throw DownloadException("$quality is no valid download quality.")

			val filePath =
				downloadFileInfoManager.getDownloadFilePath(show.mediathekShow, quality)

			// update show properties
			show.downloadId = show.id
			show.downloadedAt = DateTime.now()
			show.downloadProgress = 0

			mediathekRepository.updateShow(show)

			// TODO: check network conditions
			download(show.downloadId, downloadUrl, filePath)
		}

	override fun stopDownload(persistedShowId: Int) {
		// TODO: implement
	}

	override fun deleteDownload(persistedShowId: Int) {
		// TODO: implement
	}

	override fun deleteDownloadsWithDeletedFiles() {
		// TODO: implement
	}

	override fun getDownloadStatus(persistedShowId: Int): Flow<DownloadStatus> {
		return mediathekRepository.getDownloadStatus(persistedShowId)
	}

	override fun getDownloadProgress(persistedShowId: Int): Flow<Int> {
		return mediathekRepository.getDownloadProgress(persistedShowId)
	}

	private suspend fun download(downloadId: Int, downloadUrl: String, filePath: String) {
		mediathekRepository.updateDownloadStatus(downloadId, DownloadStatus.DOWNLOADING)

		// TODO: write progress to repository
		fileDownloader.download(downloadUrl, filePath)

		downloadFileInfoManager.markFileAsDownloaded(filePath)

		mediathekRepository.updateDownloadProgress(downloadId, 100)
		mediathekRepository.updateDownloadStatus(downloadId, DownloadStatus.COMPLETED)
	}
}
