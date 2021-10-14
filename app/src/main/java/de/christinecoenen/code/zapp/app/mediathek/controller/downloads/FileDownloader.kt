package de.christinecoenen.code.zapp.app.mediathek.controller.downloads

import android.content.Context
import android.net.Uri
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.buffer
import okio.sink
import timber.log.Timber
import java.io.FileOutputStream

class FileDownloader(
	private val applicationContext: Context,
	private val okHttpClient: OkHttpClient
) {

	/**
	 * Downloads the given file blocking to the given path. Any existing file (part) at the given
	 * path will be overwritten.
	 *
	 * @param downloadUrl file to download
	 * @param filePath either file scheme url or content scheme url for the file to download to
	 */
	fun download(downloadUrl: String, filePath: String) {
		Timber.d("Download started: %s", downloadUrl)

		// TODO: dependency inject okhttpclient
		val request = Request.Builder().url(downloadUrl).build()

		val response = okHttpClient.newCall(request).execute()

		val body = response.body!!
		// TODO: get file output stream from DownloadFileInfoManager to abstract away sdk levels
		val pfd =
			applicationContext.contentResolver.openFileDescriptor(Uri.parse(filePath), "w")
		val fileOutputStream = FileOutputStream(pfd?.fileDescriptor)
		val sink = fileOutputStream.sink().buffer()

		// TODO: track progress; see: https://github.com/square/okhttp/blob/master/samples/guide/src/main/java/okhttp3/recipes/Progress.java
		sink.writeAll(body.source())

		// TODO: set MediaStore.Video.Media.IS_PENDING to 0 in content provider
		Timber.d("Download finished: %s", downloadUrl)
	}

}
