package com.example.servivet.utils

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL

object PDFThumbnailLoader {
    suspend fun loadThumbnailFromUrl(pdfUrl: String): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                val pdfInputStream = URL(pdfUrl).openStream()
                val tempPdfFile = File.createTempFile("temp", ".pdf")
                val fileOutputStream = FileOutputStream(tempPdfFile)
                pdfInputStream.use { input ->
                    fileOutputStream.use { output ->
                        input.copyTo(output)
                    }
                }

                // Create ParcelFileDescriptor from the downloaded file
                val fileDescriptor = ParcelFileDescriptor.open(tempPdfFile, ParcelFileDescriptor.MODE_READ_ONLY)

                // Create PdfRenderer from ParcelFileDescriptor
                val pdfRenderer = PdfRenderer(fileDescriptor)
                val page = pdfRenderer.openPage(0)
                val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                page.close()
                pdfRenderer.close()
                return@withContext bitmap
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext null
            }
        }
    }
}
