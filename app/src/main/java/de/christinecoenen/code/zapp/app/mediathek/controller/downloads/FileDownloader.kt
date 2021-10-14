package de.christinecoenen.code.zapp.app.mediathek.controller.downloads

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.buffer
import okio.sink
import timber.log.Timber

class FileDownloader(
	private val okHttpClient: OkHttpClient,
	private val downloadFileInfoManager: DownloadFileInfoManager
) {

	/**
	 * Downloads the given file blocking to the given path. Any existing file (part) at the given
	 * path will be overwritten.
	 *
	 * @param downloadUrl file to download
	 * @param filePath either file scheme url or content scheme url for the file to download to
	 */
	fun download(downloadUrl: String, filePath: String) {
		Timber.d("Download started: %s to %s", downloadUrl, filePath)

		val request = Request.Builder().url(downloadUrl).build()

		val response = okHttpClient.newCall(request).execute()

		val body = response.body!!
		val fileOutputStream = downloadFileInfoManager.getFileOutputStream(filePath)
		val sink = fileOutputStream.sink().buffer()

		// TODO: track progress; see: https://github.com/square/okhttp/blob/master/samples/guide/src/main/java/okhttp3/recipes/Progress.java
		sink.writeAll(body.source())

		Timber.d("Download finished: %s", downloadUrl)
	}

}
