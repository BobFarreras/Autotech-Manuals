package com.deixebledenkaito.autotechmanuals.ui.funcionsExternes.lleguirPdf

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import java.io.File

class PdfReader(private val context: Context) {

    // Carrega un PDF i retorna una llista de Bitmap (una per pàgina)
    fun loadPdfPages(file: File, pages: List<Int>): List<Bitmap> {
        val bitmaps = mutableListOf<Bitmap>()
        val parcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
        val pdfRenderer = PdfRenderer(parcelFileDescriptor)

        try {
            for (pageNumber in pages) {
                val page = pdfRenderer.openPage(pageNumber)
                val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                bitmaps.add(bitmap)
                page.close()
            }
        } finally {
            pdfRenderer.close()
            parcelFileDescriptor.close()
        }

        return bitmaps
    }
}