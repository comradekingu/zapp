package de.christinecoenen.code.zapp.app.mediathek.controller.downloads

import android.content.Context
import android.net.Uri
import de.christinecoenen.code.zapp.app.mediathek.controller.downloads.exceptions.DownloadException
import de.christinecoenen.code.zapp.app.settings.repository.SettingsRepository
import de.christinecoenen.code.zapp.models.shows.DownloadStatus
import de.christinecoenen.code.zapp.models.shows.PersistedMediathekShow
import de.christinecoenen.code.zapp.models.shows.Quality
import de.christinecoenen.code.zapp.repositories.MediathekRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.Okio
import okio.buffer
import okio.sink
import org.joda.time.DateTime
import timber.log.Timber
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL


class ZappDownloadController(
	private val applicationContext: Context,
	private val mediathekRepository: MediathekRepository
) : IDownloadController {

	// TODO: dependency inject!
	private val settingsRepository: SettingsRepository = SettingsRepository(applicationContext)
	private val downloadFileInfoManager: DownloadFileInfoManager =
		DownloadFileInfoManager(applicationContext, settingsRepository)

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
			download(downloadUrl, filePath)
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

	// TODO: move download function to own class
	private fun download(downloadUrl: String, filePath: String) {
		// TODO: dependency inject okhttpclient
		val client = OkHttpClient()
		val request = Request.Builder().url(downloadUrl).build()

		val response = client.newCall(request).execute()

		val body = response.body!!
		// TODO: get file output stream from DownloadFileInfoManager to abstract away sdk levels
		val pfd =
			applicationContext.contentResolver.openFileDescriptor(Uri.parse(filePath), "w")
		val fileOutputStream = FileOutputStream(pfd?.fileDescriptor)
		val sink = fileOutputStream.sink().buffer()

		// TODO: track progress; see: https://github.com/square/okhttp/blob/master/samples/guide/src/main/java/okhttp3/recipes/Progress.java
		// TODO: write progress to repository
		sink.writeAll(body.source())

		// TODO: set MediaStore.Video.Media.IS_PENDING to 0 in content provider
		Timber.d("Download finished")
		// TODO: set download status to success in repository

		/*
		var count = 0

		val url = URL(downloadUrl)
		val connection: HttpURLConnection? = url.openConnection() as HttpURLConnection?
		connection?.requestMethod = "GET"
		connection?.readTimeout = 20000
		connection?.connectTimeout = 20000
		connection?.setRequestProperty("Accept-Encoding", "identity")
		connection?.useCaches = false
		connection?.connect()
		// getting file length

		if (connection != null && connection.responseCode == 200) {
			val lengthOfFile: Int = connection.contentLength

			// input stream to read file - with 8k buffer
			val input = BufferedInputStream(url.openStream());

			// Output stream to write file
			// TODO: use file output stream for older SDKs
			val pfd =
				applicationContext.contentResolver.openFileDescriptor(Uri.parse(filePath), "w")
			val fileOutputStream = FileOutputStream(pfd?.fileDescriptor)
			val outputStream = BufferedOutputStream(fileOutputStream)

			val data = ByteArray(1024)

			var total: Long = 0

			while ({ count = input.read(data); count }() != -1) {
				total += count.toLong()
				Timber.d("Progress: %d", (total * 100 / lengthOfFile).toInt())
				// writing data to file
				outputStream.write(data, 0, count)
			}

			// flushing output
			outputStream.flush()

			// closing streams
			outputStream.close()
			input.close()
			connection.disconnect()
		}*/
	}
}
