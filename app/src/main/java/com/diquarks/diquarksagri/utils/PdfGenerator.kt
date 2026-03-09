package com.diquarks.diquarksagri.utils

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.diquarks.diquarksagri.R
import com.diquarks.diquarksagri.data.PlantCalculation
import java.io.File
import java.io.FileOutputStream
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.*

object PdfGenerator {

    fun generate(
        context: Context,
        calculation: PlantCalculation,
        totalPlants: Int,
        totalPrice: Double
    ) {

        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        val primaryGreen = Color.parseColor("#2E7D32")
        val lightGray = Color.parseColor("#F2F2F2")
        val darkGray = Color.parseColor("#444444")

        val numberFormat =
            DecimalFormat("#,###", DecimalFormatSymbols(Locale.FRANCE))

        // ================= HEADER =================
        paint.color = primaryGreen
        canvas.drawRect(0f, 0f, 595f, 140f, paint)

        val logoBitmap =
            BitmapFactory.decodeResource(context.resources, R.drawable.logo_diquarks)

        val ratio =
            logoBitmap.width.toFloat() / logoBitmap.height.toFloat()

        val scaledHeight = 90
        val scaledWidth = (scaledHeight * ratio).toInt()

        val scaledLogo =
            Bitmap.createScaledBitmap(logoBitmap, scaledWidth, scaledHeight, true)

        canvas.drawBitmap(scaledLogo, 40f, 25f, null)

        paint.color = Color.WHITE
        paint.textSize = 22f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("ESTIMATION DES PLANTS", 595f / 2f, 75f, paint)
        paint.textAlign = Paint.Align.LEFT

        paint.color = Color.BLACK
        paint.textSize = 11f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.textAlign = Paint.Align.RIGHT

        val date =
            SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE).format(Date())

        val devisNumber =
            "DEV-${System.currentTimeMillis().toString().takeLast(6)}"

        canvas.drawText("Date: $date", 565f, 60f, paint)
        canvas.drawText("N° Devis: $devisNumber", 565f, 78f, paint)

        paint.textAlign = Paint.Align.LEFT

        // ================= CLIENT INFO =================
        paint.color = Color.BLACK
        paint.textSize = 14f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("INFORMATIONS CLIENT", 40f, 190f, paint)

        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.color = darkGray

        canvas.drawText(
            "Nom de l'acheteur: ${calculation.buyerName}",
            40f,
            215f,
            paint
        )
        canvas.drawText("Localisation: Maroc", 40f, 235f, paint)

        // ================= TABLE =================
        var y = 280f

        paint.color = lightGray
        canvas.drawRect(40f, y, 555f, y + 30f, paint)

        paint.color = Color.BLACK
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 12f

        canvas.drawText("DÉSIGNATION", 60f, y + 20f, paint)
        canvas.drawText("VALEUR", 400f, y + 20f, paint)

        y += 40f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)

        val details = listOf(
            "Plante" to calculation.plantName,
            "Surface Totale" to "${calculation.surface} Ha",
            "Densité de base" to "${numberFormat.format(calculation.density)} plants/Ha",
            "Mode de plantation" to if (calculation.isDense) "Dense (+20%)" else "Standard",
            "Quantité Totale" to "${numberFormat.format(totalPlants)} Plants"
        )

        for (detail in details) {
            canvas.drawText(detail.first, 60f, y, paint)
            canvas.drawText(detail.second, 400f, y, paint)

            paint.color = Color.parseColor("#DDDDDD")
            canvas.drawLine(40f, y + 10f, 555f, y + 10f, paint)
            paint.color = Color.BLACK
            y += 35f
        }

        // ================= TOTAL =================
        y += 30f

        paint.color = primaryGreen
        canvas.drawRect(300f, y, 555f, y + 45f, paint)

        paint.color = Color.WHITE
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 16f
        paint.textAlign = Paint.Align.CENTER

        val formattedPrice =
            String.format(Locale.FRANCE, "%,.2f DH", totalPrice)

        canvas.drawText(
            "TOTAL : $formattedPrice",
            427f,
            y + 28f,
            paint
        )

        paint.textAlign = Paint.Align.LEFT

        // ================= RECOMMANDATIONS =================
        y += 90f

        paint.color = primaryGreen
        canvas.drawRoundRect(40f, y, 555f, y + 35f, 12f, 12f, paint)

        paint.color = Color.WHITE
        paint.textSize = 14f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("RECOMMANDATIONS", 60f, y + 23f, paint)

        y += 55f
        paint.color = Color.BLACK
        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)

        val recommandations = listOf(
            "Distance de plantation 40 cm",
            "Éviter l'arrosage excessif",
            "Fertilisation équilibrée",
            "Protection contre les insectes"
        )

        for (rec in recommandations) {
            canvas.drawCircle(60f, y - 5f, 5f, paint)
            canvas.drawText(rec, 80f, y, paint)
            y += 30f
        }

        // ================= FOOTER =================
        paint.color = Color.parseColor("#F4F4F4")
        canvas.drawRect(0f, 785f, 595f, 842f, paint)

        paint.color = Color.GRAY
        paint.textSize = 9f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
        paint.textAlign = Paint.Align.CENTER

        canvas.drawText(
            "Ce document est une estimation valable 30 jours à compter de la date d'émission.",
            595f / 2f,
            805f,
            paint
        )

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.color = primaryGreen

        canvas.drawText(
            "Contact: +212 661 93 66 26  |  Email: contact@diquarks.com",
            595f / 2f,
            825f,
            paint
        )

        pdfDocument.finishPage(page)
        saveAndOpenPdf(context, pdfDocument, devisNumber)
    }

    private fun saveAndOpenPdf(
        context: Context,
        pdfDocument: PdfDocument,
        devisNumber: String
    ) {

        val file =
            File(context.getExternalFilesDir(null), "Devis_$devisNumber.pdf")

        try {
            pdfDocument.writeTo(FileOutputStream(file))

            val uri =
                FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    file
                )

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(intent)

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            pdfDocument.close()
        }
    }
}