package com.deixebledenkaito.autotechmanuals.ui.funcionsExternes.autoMidesImg

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import android.graphics.BitmapFactory
import android.provider.MediaStore
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import androidx.navigation.NavController
import com.deixebledenkaito.autotechmanuals.domain.DetectedObject
import com.deixebledenkaito.autotechmanuals.domain.Dimensions
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import java.io.InputStream

import org.opencv.dnn.Dnn
import org.opencv.dnn.Net
import org.opencv.imgcodecs.Imgcodecs
import java.io.FileOutputStream

@Composable
fun CameraSizeDetectorApp(navController: NavController) {
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var objectSize by remember { mutableStateOf<String?>(null) }
    var isAnalyzing by remember { mutableStateOf(false) }
    var analyzeImageTrigger by remember { mutableStateOf(false) }
    var processedImage by remember { mutableStateOf<Bitmap?>(null) }

    fun createImageFile(context: Context): File {
        val storageDir = context.getExternalFilesDir("images")
        if (storageDir != null && !storageDir.exists()) {
            storageDir.mkdirs()
        }
        return File.createTempFile("JPEG_${System.currentTimeMillis()}_", ".jpg", storageDir)
    }

    val takePictureLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            isAnalyzing = true
            analyzeImageTrigger = true
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            val photoFile = createImageFile(context)
            imageUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", photoFile)
            imageUri?.let { takePictureLauncher.launch(it) }
        }) {
            Text("Fer una foto")
        }

        Spacer(modifier = Modifier.height(16.dp))

        when {
            isAnalyzing -> Text("Analitzant la imatge...")
            objectSize != null -> {
                Text(objectSize!!)
                processedImage?.let { bitmap ->
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Imatge processada",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }

    LaunchedEffect(analyzeImageTrigger) {
        if (analyzeImageTrigger) {
            imageUri?.let { uri ->
                val (sizeText, bitmap) = analyzeImage(context, uri)
                objectSize = sizeText
                processedImage = bitmap
                isAnalyzing = false
            }
            analyzeImageTrigger = false
        }
    }
}

suspend fun analyzeImage(context: Context, imageUri: Uri): Pair<String, Bitmap> {
    return withContext(Dispatchers.IO) {
        try {
            Log.d("OpenCV", "Carregant imatge des de URI: $imageUri")
            val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            Log.d("OpenCV", "Imatge carregada correctament")

            // Converteix la imatge a 3 canals (RGB)
            val rgbBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, false)
            val mat = Mat()
            Utils.bitmapToMat(rgbBitmap, mat)
            Log.d("OpenCV", "Bitmap convertit a Mat")

            // Converteix la imatge a 3 canals (RGB) si té 4 canals (RGBA)
            if (mat.channels() == 4) {
                Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGBA2RGB)
                Log.d("OpenCV", "Imatge convertida de RGBA a RGB")
            }

            // Carrega el model YOLO i les classes
            val net = loadYoloModel(context)
            val classes = loadClasses(context)

            // Preprocessa la imatge
            val blob = Dnn.blobFromImage(mat, 1.0 / 255.0, Size(416.0, 416.0), Scalar(0.0), true, false)
            net.setInput(blob)

            // Obten les capes de sortida no connectades (les que produeixen les deteccions)
            val outputLayerNames = net.getUnconnectedOutLayersNames()

            // Detecta objectes
            val detections = mutableListOf<Mat>()
            net.forward(detections, outputLayerNames)

            val detectedObjects = mutableListOf<DetectedObject>()

            // Processa les deteccions
            for (detection in detections) {
                for (i in 0 until detection.rows()) {
                    val scores = detection.row(i).colRange(5, detection.cols())
                    val classId = scores.argmax() // Utilitza la funció argmax()
                    val confidence = scores.get(0, classId)[0]

                    if (confidence > 0.95) { // Filtra per confiança
                        val centerX = detection.get(i, 0)[0] * mat.cols()
                        val centerY = detection.get(i, 1)[0] * mat.rows()
                        val width = detection.get(i, 2)[0] * mat.cols()
                        val height = detection.get(i, 3)[0] * mat.rows()

                        val x = (centerX - width / 2).toInt()
                        val y = (centerY - height / 2).toInt()

                        val objectWidthCm = width * 0.1 // Escala de píxels a cm (ajusta segons la teva necessitat)
                        val objectHeightCm = height * 0.1

                        val label = classes[classId] // Utilitza la llista de classes carregada
                        detectedObjects.add(
                            DetectedObject(
                                label = label,
                                dimensions = Dimensions(objectWidthCm, objectHeightCm),
                                confidence = confidence,
                                centerX = centerX,
                                centerY = centerY,
                                width = width,
                                height = height
                            )
                        )
                    }
                }
            }

            // Dibuixa les deteccions a la imatge
            val outputMat = drawDetectionsOnImage(mat, detectedObjects)
            val outputBitmap = Bitmap.createBitmap(outputMat.cols(), outputMat.rows(), Bitmap.Config.ARGB_8888)
            Utils.matToBitmap(outputMat, outputBitmap)
            saveProcessedImage(context, outputBitmap)

            Log.d("OpenCV", "Imatge processada correctament")
            Pair(
                detectedObjects.joinToString("\n") { "${it.label}: ${"%.2f".format(it.dimensions.width)} cm x ${"%.2f".format(it.dimensions.height)} cm" },
                outputBitmap
            )
        } catch (e: Exception) {
            Log.e("OpenCV", "Error durant l'anàlisi de la imatge: ${e.message}", e)
            Pair("Error: ${e.message}", Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888))
        }
    }
}
fun drawDetectionsOnImage(mat: Mat, detectedObjects: List<DetectedObject>): Mat {
    for (obj in detectedObjects) {
        val label = obj.label
        val dimensions = obj.dimensions
        val confidence = obj.confidence

        // Coordenades de l'objecte detectat
        val x = (obj.centerX - obj.width / 2).toInt()
        val y = (obj.centerY - obj.height / 2).toInt()

        // Dibuixa el rectangle al voltant de l'objecte
        Imgproc.rectangle(
            mat,
            Point(x.toDouble(), y.toDouble()),
            Point((x + obj.width).toDouble(), (y + obj.height).toDouble()),
            Scalar(0.0, 255.0, 0.0), // Color verd
            4 // Gruix de la línia
        )

        // Dibuixa l'etiqueta i les dimensions
        Imgproc.putText(
            mat,
            "$label: ${"%.2f".format(dimensions.width)} cm x ${"%.2f".format(dimensions.height)} cm (${"%.2f".format(confidence)})",
            Point(x.toDouble(), y.toDouble() - 20), // Posició del text
            Imgproc.FONT_HERSHEY_SIMPLEX,
            4.0, // Mida de la font
            Scalar(0.0, 255.0, 0.0), // Color verd
            4 // Gruix de la línia
        )
    }
    return mat
}

fun saveProcessedImage(context: Context, bitmap: Bitmap) {
    Log.d("OpenCV", "Guardant imatge processada...")
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "processed_image.jpg")
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
    }
    val resolver = context.contentResolver
    val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
    uri?.let {
        resolver.openOutputStream(it)?.use { stream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        }
    }
}

fun Mat.argmax(): Int {
    val scores = FloatArray(this.cols())
    this.get(0, 0, scores) // Obté les puntuacions de la fila actual
    var maxIndex = 0
    var maxScore = scores[0]
    for (i in 1 until scores.size) {
        if (scores[i] > maxScore) {
            maxScore = scores[i]
            maxIndex = i
        }
    }
    return maxIndex
}

fun loadClasses(context: Context): List<String> {
    return try {
        context.assets.open("coco_catala.names").bufferedReader().useLines { it.toList() }
    } catch (e: Exception) {
        Log.e("loadClasses", "Error carregant les classes: ${e.message}")
        emptyList()
    }
}
fun loadYoloModel(context: Context): Net {
    return try {
        // Carrega el fitxer de configuració (.cfg) des de assets
        val cfgInputStream: InputStream = context.assets.open("yolov3.cfg")
        val cfgFile = File.createTempFile("yolov3", ".cfg", context.cacheDir)
        cfgInputStream.use { input ->
            FileOutputStream(cfgFile).use { output ->
                input.copyTo(output)
            }
        }

        // Carrega el fitxer de pesos (.weights) des de assets
        val weightsInputStream: InputStream = context.assets.open("yolov3.weights")
        val weightsFile = File.createTempFile("yolov3", ".weights", context.cacheDir)
        weightsInputStream.use { input ->
            FileOutputStream(weightsFile).use { output ->
                input.copyTo(output)
            }
        }

        // Carrega el model YOLO utilitzant els fitxers temporals
        val net = Dnn.readNetFromDarknet(cfgFile.absolutePath, weightsFile.absolutePath)

        // Verifica si el model s'ha carregat correctament
        if (net.empty()) {
            throw IllegalStateException("No s'ha pogut carregar el model YOLO.")
        }

        Log.d("loadYoloModel", "Model YOLO carregat correctament")
        net
    } catch (e: Exception) {
        Log.e("loadYoloModel", "Error carregant el model YOLO: ${e.message}")
        throw e
    }
}
