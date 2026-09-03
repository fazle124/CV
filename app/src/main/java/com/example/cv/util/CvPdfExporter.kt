package com.example.cv.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintManager
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.cv.R
import com.example.cv.model.CvHeader
import com.example.cv.model.CvSection
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CvPdfExporter {

    // A4 size at 72 DPI is 595 x 842 points
    private const val PAGE_WIDTH = 595
    private const val PAGE_HEIGHT = 842
    private const val MARGIN_X = 40f
    private const val MARGIN_Y = 40f
    private const val CONTENT_WIDTH = PAGE_WIDTH - (MARGIN_X * 2)

    fun exportAndSharePdf(context: Context, header: CvHeader, sections: List<CvSection>) {
        try {
            val file = generatePdfFile(context, header, sections)
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Resume of ${header.name}")
                putExtra(Intent.EXTRA_TEXT, "Attached is the Resume of ${header.name}.")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share or Save Resume PDF"))
        } catch (e: Exception) {
            // If FileProvider is not configured, fall back to system PrintManager
            printCvDocument(context, header, sections)
        }
    }

    fun printCvDocument(context: Context, header: CvHeader, sections: List<CvSection>) {
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager
        if (printManager == null) {
            Toast.makeText(context, "Printing not supported on this device", Toast.LENGTH_SHORT).show()
            return
        }

        val printAdapter = object : PrintDocumentAdapter() {
            private var pdfDocument: PdfDocument? = null

            override fun onLayout(
                oldAttributes: PrintAttributes?,
                newAttributes: PrintAttributes?,
                cancellationSignal: android.os.CancellationSignal?,
                callback: LayoutResultCallback?,
                extras: android.os.Bundle?
            ) {
                if (cancellationSignal?.isCanceled == true) {
                    callback?.onLayoutCancelled()
                    return
                }

                val builder = android.print.PrintDocumentInfo.Builder("Resume_of_${header.name.replace(" ", "_")}.pdf")
                    .setContentType(android.print.PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .setPageCount(android.print.PrintDocumentInfo.PAGE_COUNT_UNKNOWN)
                    .build()

                callback?.onLayoutFinished(builder, true)
            }

            override fun onWrite(
                pages: Array<out android.print.PageRange>?,
                destination: android.os.ParcelFileDescriptor?,
                cancellationSignal: android.os.CancellationSignal?,
                callback: WriteResultCallback?
            ) {
                if (cancellationSignal?.isCanceled == true) {
                    callback?.onWriteCancelled()
                    return
                }

                try {
                    val pdfDoc = buildPdfDocument(context, header, sections)
                    destination?.let {
                        FileOutputStream(it.fileDescriptor).use { outputStream ->
                            pdfDoc.writeTo(outputStream)
                        }
                    }
                    pdfDoc.close()
                    callback?.onWriteFinished(arrayOf(android.print.PageRange.ALL_PAGES))
                } catch (e: Exception) {
                    callback?.onWriteFailed(e.message)
                }
            }
        }

        printManager.print(
            "Resume_${header.name.replace(" ", "_")}",
            printAdapter,
            PrintAttributes.Builder()
                .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                .build()
        )
    }

    private fun generatePdfFile(context: Context, header: CvHeader, sections: List<CvSection>): File {
        val pdfDoc = buildPdfDocument(context, header, sections)
        val cacheDir = File(context.cacheDir, "documents").apply { mkdirs() }
        val file = File(cacheDir, "Resume_of_${header.name.replace(" ", "_")}.pdf")
        FileOutputStream(file).use { out ->
            pdfDoc.writeTo(out)
        }
        pdfDoc.close()
        return file
    }

    private fun buildPdfDocument(context: Context, header: CvHeader, sections: List<CvSection>): PdfDocument {
        val document = PdfDocument()
        var pageNumber = 1

        val titlePaint = Paint().apply {
            color = Color.parseColor("#171717")
            textSize = 18f
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            isAntiAlias = true
        }

        val namePaint = Paint().apply {
            color = Color.parseColor("#0B69FF")
            textSize = 20f
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            isAntiAlias = true
        }

        val sectionHeaderPaint = Paint().apply {
            color = Color.parseColor("#171717")
            textSize = 12f
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            isAntiAlias = true
        }

        val boldBodyPaint = Paint().apply {
            color = Color.parseColor("#1F2937")
            textSize = 9.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val regularBodyPaint = Paint().apply {
            color = Color.parseColor("#374151")
            textSize = 9f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
        }

        val lineDividerPaint = Paint().apply {
            color = Color.parseColor("#9B9B9B")
            strokeWidth = 1f
            isAntiAlias = true
        }

        val footerPaint = Paint().apply {
            color = Color.parseColor("#777777")
            textSize = 8f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }

        var pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
        var page = document.startPage(pageInfo)
        var canvas = page.canvas

        var cursorY = MARGIN_Y

        fun checkPageBreak(neededHeight: Float) {
            if (cursorY + neededHeight > PAGE_HEIGHT - MARGIN_Y) {
                canvas.drawText("Page $pageNumber", PAGE_WIDTH / 2f, PAGE_HEIGHT - 20f, footerPaint)
                document.finishPage(page)
                pageNumber++
                pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
                page = document.startPage(pageInfo)
                canvas = page.canvas
                cursorY = MARGIN_Y
            }
        }

        // Draw Header
        canvas.drawText("Resume of", MARGIN_X, cursorY, titlePaint)
        cursorY += 22f
        canvas.drawText(header.name, MARGIN_X, cursorY, namePaint)
        cursorY += 16f

        canvas.drawText("Mailing Address: ${header.address}", MARGIN_X, cursorY, regularBodyPaint)
        cursorY += 13f
        canvas.drawText("Contact: ${header.phone}   |   E-mail: ${header.email}", MARGIN_X, cursorY, regularBodyPaint)
        cursorY += 13f
        if (header.linkedin.isNotEmpty()) {
            canvas.drawText("LinkedIn: ${header.linkedin}", MARGIN_X, cursorY, regularBodyPaint)
            cursorY += 13f
        }

        // Try drawing Profile image on the right
        try {
            val profileRes = R.drawable.profile
            val bitmap = BitmapFactory.decodeResource(context.resources, profileRes)
            if (bitmap != null) {
                val scaled = Bitmap.createScaledBitmap(bitmap, 65, 80, true)
                canvas.drawBitmap(scaled, PAGE_WIDTH - MARGIN_X - 65f, MARGIN_Y, null)
            }
        } catch (e: Exception) {
            // Profile image fallback
        }

        cursorY += 8f
        canvas.drawLine(MARGIN_X, cursorY, PAGE_WIDTH - MARGIN_X, cursorY, lineDividerPaint)
        cursorY += 16f

        // Draw Sections
        for (sec in sections) {
            if (sec.title.isBlank() && sec.body.isBlank()) continue
            checkPageBreak(30f)

            canvas.drawText(sec.title.uppercase(Locale.ROOT), MARGIN_X, cursorY, sectionHeaderPaint)
            cursorY += 4f
            canvas.drawLine(MARGIN_X, cursorY, PAGE_WIDTH - MARGIN_X, cursorY, lineDividerPaint)
            cursorY += 12f

            val lines = sec.body.split("\n")
            for (rawLine in lines) {
                val line = rawLine.trim()
                if (line.isEmpty()) {
                    cursorY += 5f
                    continue
                }

                checkPageBreak(14f)

                if (line.startsWith("•") || line.startsWith("-") || line.startsWith("*")) {
                    val bulletText = line.replaceFirst(Regex("^[•\\-*]\\s*"), "")
                    canvas.drawText("•", MARGIN_X + 6f, cursorY, boldBodyPaint)
                    canvas.drawText(bulletText, MARGIN_X + 16f, cursorY, regularBodyPaint)
                    cursorY += 12f
                } else if (line.contains(":") && !line.startsWith("http")) {
                    val parts = line.split(":", limit = 2)
                    val label = parts[0].trim()
                    val value = parts.getOrNull(1)?.trim() ?: ""

                    if (label.equals("Signature", ignoreCase = true)) {
                        checkPageBreak(40f)
                        canvas.drawText("Signature:", MARGIN_X, cursorY + 14f, boldBodyPaint)
                        try {
                            val sigBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.signature)
                            if (sigBitmap != null) {
                                val scaledSig = Bitmap.createScaledBitmap(sigBitmap, 90, 40, true)
                                canvas.drawBitmap(scaledSig, MARGIN_X + 60f, cursorY - 8f, null)
                            }
                        } catch (e: Exception) {}
                        cursorY += 36f
                    } else if (label.equals("Date", ignoreCase = true)) {
                        val todayStr = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                        canvas.drawText("Date : $todayStr", MARGIN_X, cursorY, boldBodyPaint)
                        cursorY += 12f
                    } else {
                        canvas.drawText("$label :", MARGIN_X, cursorY, boldBodyPaint)
                        canvas.drawText(value, MARGIN_X + 120f, cursorY, regularBodyPaint)
                        cursorY += 12f
                    }
                } else if (line.startsWith("Colossus Apparel") || line.startsWith("Pacific Quality") ||
                    line.startsWith("Bachelor of") || line.startsWith("Higher Secondary") ||
                    line.startsWith("Secondary School")
                ) {
                    canvas.drawText(line, MARGIN_X, cursorY, boldBodyPaint)
                    cursorY += 13f
                } else {
                    canvas.drawText(line, MARGIN_X, cursorY, regularBodyPaint)
                    cursorY += 12f
                }
            }
            cursorY += 10f
        }

        canvas.drawText("Page $pageNumber", PAGE_WIDTH / 2f, PAGE_HEIGHT - 20f, footerPaint)
        document.finishPage(page)

        return document
    }
}
