package com.steply.app.ui.screens.remote

import android.content.ContentResolver
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.SystemClock
import android.widget.VideoView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.steply.app.remote.RemoteCameraStreamer
import com.steply.app.ui.screens.components.SteplyCorners
import java.io.ByteArrayOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import kotlin.math.roundToLong

@Composable
fun GalleryVideoStreamPreview(
    videoUri: Uri?,
    remoteCameraStreamer: RemoteCameraStreamer?,
    onVideoStatus: (String) -> Unit,
    onVideoError: (String) -> Unit,
    onFrameSent: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val currentOnStatus by rememberUpdatedState(onVideoStatus)
    val currentOnError by rememberUpdatedState(onVideoError)
    val currentOnFrameSent by rememberUpdatedState(onFrameSent)
    val videoView = remember(context) {
        VideoView(context).apply {
            contentDescription = "Selected demo video preview for PC streaming"
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(3f / 4f)
            .clip(RoundedCornerShape(SteplyCorners.Card))
            .background(androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant)
            .border(
                width = 1.dp,
                color = androidx.compose.material3.MaterialTheme.colorScheme.outline.copy(alpha = 0.42f),
                shape = RoundedCornerShape(SteplyCorners.Card),
            )
            .padding(1.dp),
    ) {
        AndroidView(
            factory = { videoView },
            update = { view ->
                if (videoUri != null) {
                    view.setVideoURI(videoUri)
                    view.setOnPreparedListener { player ->
                        player.isLooping = true
                        player.setVolume(0f, 0f)
                        view.start()
                        currentOnStatus("Demo video preview and stream are ready.")
                    }
                    view.setOnErrorListener { _, _, _ ->
                        currentOnError("Could not preview the selected demo video.")
                        true
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(3f / 4f)
                .clip(RoundedCornerShape(SteplyCorners.Card)),
        )
    }

    DisposableEffect(videoUri, videoView) {
        onDispose {
            videoView.stopPlayback()
        }
    }

    LaunchedEffect(context, videoUri, remoteCameraStreamer) {
        val activeUri = videoUri ?: return@LaunchedEffect
        val activeStreamer = remoteCameraStreamer ?: return@LaunchedEffect
        var lastSendFailureReportedAt = 0L
        currentOnStatus("Preparing demo video frames for PC streaming.")
        val metadata = withContext(Dispatchers.IO) {
            readVideoMetadata(context.contentResolver, activeUri)
        }
        val sampleIntervalMs = metadata.sampleIntervalMs
        val startedAt = SystemClock.uptimeMillis()
        var lastSampleIndex = -1L

        while (isActive) {
            val elapsedMs = SystemClock.uptimeMillis() - startedAt
            val sampleIndex = elapsedMs / sampleIntervalMs
            if (sampleIndex == lastSampleIndex) {
                delay((sampleIntervalMs - (elapsedMs % sampleIntervalMs)).coerceAtLeast(1L))
                continue
            }
            lastSampleIndex = sampleIndex

            var positionUs = sampleIndex * sampleIntervalMs * 1_000L
            if (metadata.durationUs > 0L) {
                positionUs %= metadata.durationUs
            }
            val frame = withContext(Dispatchers.IO) {
                extractVideoFrame(context.contentResolver, activeUri, positionUs)
            }
            if (frame == null) {
                currentOnError("Could not read frames from the selected demo video.")
                return@LaunchedEffect
            }
            if (SystemClock.uptimeMillis() - frame.receivedAtMs > STALE_FRAME_TIMEOUT_MS) {
                lastSampleIndex = -1L
                continue
            }

            val sent = withContext(Dispatchers.IO) {
                activeStreamer.sendJpeg(
                    bytes = frame.jpegBytes,
                    capturedAtMs = frame.receivedAtMs,
                    maxFrameAgeMs = STALE_FRAME_TIMEOUT_MS,
                )
            }
            val now = SystemClock.uptimeMillis()
            if (sent) {
                currentOnFrameSent()
            } else if (now - lastSendFailureReportedAt >= SEND_FAILURE_REPORT_INTERVAL_MS) {
                lastSendFailureReportedAt = now
                currentOnError("Frame extracted, but the PC connection is not ready yet.")
            }
        }
    }
}

private fun extractVideoFrame(
    contentResolver: ContentResolver,
    uri: Uri,
    positionUs: Long,
    maxWidth: Int = 640,
    quality: Int = 62,
): DemoVideoFrame? {
    val retriever = MediaMetadataRetriever()
    return try {
        contentResolver.openFileDescriptor(uri, "r")?.use { parcelFileDescriptor ->
            val receivedAtMs = SystemClock.uptimeMillis()
            retriever.setDataSource(parcelFileDescriptor.fileDescriptor)
            val bitmap = retriever.getFrameAtTime(positionUs, MediaMetadataRetriever.OPTION_CLOSEST)
                ?: return null
            DemoVideoFrame(
                jpegBytes = bitmap.toJpegBytes(maxWidth = maxWidth, quality = quality),
                receivedAtMs = receivedAtMs,
            )
        }
    } catch (_: Throwable) {
        null
    } finally {
        retriever.release()
    }
}

private fun readVideoMetadata(
    contentResolver: ContentResolver,
    uri: Uri,
): VideoStreamMetadata {
    val retriever = MediaMetadataRetriever()
    return try {
        contentResolver.openFileDescriptor(uri, "r")?.use { parcelFileDescriptor ->
            retriever.setDataSource(parcelFileDescriptor.fileDescriptor)
            val durationUs = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                ?.toLongOrNull()
                ?.times(1_000L)
                ?: 0L
            val frameRate = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CAPTURE_FRAMERATE)
                ?.toFloatOrNull()
                ?.takeIf { it > 0f }
                ?: DEFAULT_VIDEO_FRAME_RATE
            val sampleIntervalMs = ((FRAMES_PER_DEMO_SAMPLE * 1_000f) / frameRate)
                .roundToLong()
                .coerceAtLeast(1L)
            VideoStreamMetadata(
                durationUs = durationUs,
                sampleIntervalMs = sampleIntervalMs,
            )
        } ?: VideoStreamMetadata()
    } catch (_: Throwable) {
        VideoStreamMetadata()
    } finally {
        retriever.release()
    }
}

private data class VideoStreamMetadata(
    val durationUs: Long = 0L,
    val sampleIntervalMs: Long = DEFAULT_DEMO_SAMPLE_INTERVAL_MS,
)

private data class DemoVideoFrame(
    val jpegBytes: ByteArray,
    val receivedAtMs: Long,
)

private fun Bitmap.toJpegBytes(
    maxWidth: Int,
    quality: Int,
): ByteArray {
    val scaledBitmap = if (width > maxWidth) {
        val targetHeight = (height * (maxWidth.toFloat() / width)).toInt().coerceAtLeast(1)
        Bitmap.createScaledBitmap(this, maxWidth, targetHeight, true)
    } else {
        this
    }

    return ByteArrayOutputStream().use { output ->
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, output)
        if (scaledBitmap !== this) scaledBitmap.recycle()
        recycle()
        output.toByteArray()
    }
}

private const val FRAMES_PER_DEMO_SAMPLE = 5
private const val DEFAULT_VIDEO_FRAME_RATE = 30f
private const val DEFAULT_DEMO_SAMPLE_INTERVAL_MS = 167L
private const val STALE_FRAME_TIMEOUT_MS = 500L
private const val SEND_FAILURE_REPORT_INTERVAL_MS = 1_000L
