package com.dataguard.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.dataguard.data.entity.UsageEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CsvExporter @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun exportToCSV(usageList: List<UsageEntity>): Uri? {
        return try {
            val fileName = "dataguard_export_${System.currentTimeMillis()}.csv"
            val file = File(context.cacheDir, fileName)

            file.bufferedWriter().use { writer ->
                // Write header
                writer.write("Timestamp,Date,Total TX (Bytes),Total RX (Bytes),Total (MB),Period\n")

                // Write data
                for (usage in usageList) {
                    val date = DataFormatter.formatDate(usage.timestamp)
                    val totalMB = (usage.totalMobileTx + usage.totalMobileRx) / (1024.0 * 1024.0)
                    writer.write(
                        "${usage.timestamp},$date,${usage.totalMobileTx},${usage.totalMobileRx}," +
                                String.format("%.2f", totalMB) + ",${usage.period}\n"
                    )
                }
            }

            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun shareCSV(uri: Uri) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share Data Usage Report"))
    }
}
