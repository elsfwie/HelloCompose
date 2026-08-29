// ================================================
// Coroutine Demo - DownloaderScreen (Section 2.10)
// Perlu Android Studio (Jetpack Compose + Coroutines)
// Taruh file ini di package Compose project kamu,
// lalu panggil DownloaderScreen() dari setContent { }
// ================================================

package com.example.coroutine

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.*

// Status yang mungkin terjadi selama proses download
sealed class DownloadStatus {
    object Idle : DownloadStatus()
    data class InProgress(val progress: Int) : DownloadStatus()
    object Completed : DownloadStatus()
    object Cancelled : DownloadStatus()
}

@Composable
fun DownloaderScreen() {
    // State untuk status download saat ini
    var status by remember { mutableStateOf<DownloadStatus>(DownloadStatus.Idle) }

    // Job untuk menyimpan referensi coroutine yang sedang berjalan,
    // supaya bisa di-cancel dari tombol Cancel
    var downloadJob by remember { mutableStateOf<Job?>(null) }

    // Scope yang mengikuti lifecycle composable ini
    val scope = rememberCoroutineScope()

    // Fungsi untuk memulai proses download simulasi
    fun startDownload() {
        // Kalau ada job lama yang masih jalan, hentikan dulu
        downloadJob?.cancel()

        downloadJob = scope.launch {
            status = DownloadStatus.InProgress(0)
            try {
                for (progress in 1..10) {
                    delay(500) // simulasi kerja download per langkah
                    // ensureActive() akan otomatis melempar CancellationException
                    // jika job sudah di-cancel, sehingga loop berhenti
                    ensureActive()
                    status = DownloadStatus.InProgress(progress * 10)
                }
                status = DownloadStatus.Completed
            } catch (e: CancellationException) {
                status = DownloadStatus.Cancelled
                throw e // penting: rethrow supaya coroutine machinery tahu job dibatalkan
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Pesan status yang berbeda tergantung kondisi
        val pesan = when (val currentStatus = status) {
            is DownloadStatus.Idle -> "Tekan tombol untuk mulai download"
            is DownloadStatus.InProgress -> "Mengunduh... ${currentStatus.progress}%"
            is DownloadStatus.Completed -> "Download selesai!"
            is DownloadStatus.Cancelled -> "Download dibatalkan oleh pengguna"
        }
        Text(text = pesan, style = MaterialTheme.typography.bodyLarge)

        Spacer(modifier = Modifier.height(16.dp))

        if (status is DownloadStatus.InProgress) {
            LinearProgressIndicator(
                progress = { (status as DownloadStatus.InProgress).progress / 100f },
                modifier = Modifier.fillMaxWidth(0.8f)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = { startDownload() }) {
                Text("Start Download")
            }

            // Tombol Cancel menghentikan coroutine yang sedang berjalan
            Button(
                onClick = { downloadJob?.cancel() },
                enabled = status is DownloadStatus.InProgress
            ) {
                Text("Cancel")
            }
        }
    }
}