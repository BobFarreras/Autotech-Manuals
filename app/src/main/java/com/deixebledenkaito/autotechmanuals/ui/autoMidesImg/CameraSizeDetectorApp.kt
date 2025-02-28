package com.deixebledenkaito.autotechmanuals.ui.autoMidesImg

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
import androidx.navigation.NavController
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
            objectSize != null -> Text(objectSize!!)
        }
    }

    LaunchedEffect(analyzeImageTrigger) {
        if (analyzeImageTrigger) {
            imageUri?.let { uri ->
                objectSize = analyzeImage(context, uri)
                isAnalyzing = false
            }
            analyzeImageTrigger = false
        }
    }
}

// Funció per carregar el model YOLO des de la carpeta assets
fun loadYoloModel(context: Context): Net {
    // Carrega el fitxer de configuració (.cfg)
    val cfgInputStream: InputStream = context.assets.open("yolov3.cfg")
    val cfgFile = File.createTempFile("yolov3", ".cfg")
    cfgInputStream.use { input ->
        FileOutputStream(cfgFile).use { output ->
            input.copyTo(output)
        }
    }

    // Carrega el fitxer de pesos (.weights)
    val weightsInputStream: InputStream = context.assets.open("yolov3.weights")
    val weightsFile = File.createTempFile("yolov3", ".weights")
    weightsInputStream.use { input ->
        FileOutputStream(weightsFile).use { output ->
            input.copyTo(output)
        }
    }

    // Carrega el model YOLO
    return Dnn.readNetFromDarknet(cfgFile.absolutePath, weightsFile.absolutePath)
}

// Funció per carregar les classes des de la carpeta assets
fun loadClasses(context: Context): List<String> {
    return context.assets.open("coco.names").bufferedReader().useLines { it.toList() }
}

// Modifica la funció analyzeImage per utilitzar les funcions anteriors
suspend fun analyzeImage(context: Context, imageUri: Uri): String {
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
                    val classId = scores.argmax()
                    val confidence = scores.get(0, classId)[0]

                    if (confidence > 0.5) { // Filtra per confiança
                        val centerX = detection.get(i, 0)[0] * mat.cols()
                        val centerY = detection.get(i, 1)[0] * mat.rows()
                        val width = detection.get(i, 2)[0] * mat.cols()
                        val height = detection.get(i, 3)[0] * mat.rows()

                        val x = (centerX - width / 2).toInt()
                        val y = (centerY - height / 2).toInt()

                        val objectWidthCm = width * 0.1 // Escala de píxels a cm (ajusta segons la teva necessitat)
                        val objectHeightCm = height * 0.1

                        val label = classes[classId.toInt()]
                        detectedObjects.add(DetectedObject(label, Dimensions(objectWidthCm, objectHeightCm), confidence))
                    }
                }
            }

            // Dibuixa les deteccions a la imatge
            val outputMat = drawDetectionsOnImage(mat, detectedObjects)
            val outputBitmap = Bitmap.createBitmap(outputMat.cols(), outputMat.rows(), Bitmap.Config.ARGB_8888)
            Utils.matToBitmap(outputMat, outputBitmap)
            saveProcessedImage(context, outputBitmap)

            Log.d("OpenCV", "Imatge processada correctament")
            detectedObjects.joinToString("\n") { "${it.label}: ${"%.2f".format(it.dimensions.width)} cm x ${"%.2f".format(it.dimensions.height)} cm" }
        } catch (e: Exception) {
            Log.e("OpenCV", "Error durant l'anàlisi de la imatge: ${e.message}", e)
            "Error: ${e.message}"
        }
    }
}

fun drawDetectionsOnImage(mat: Mat, detectedObjects: List<DetectedObject>): Mat {
    for ((index, obj) in detectedObjects.withIndex()) {
        val label = obj.label
        val dimensions = obj.dimensions
        val confidence = obj.confidence

        // Dibuixa el rectangle al voltant de l'objecte
        Imgproc.rectangle(
            mat,
            Point(50.0, 50.0 + index * 30.0),
            Point(250.0, 80.0 + index * 30.0),
            Scalar(0.0, 255.0, 0.0),
            2
        )

        // Dibuixa l'etiqueta i les dimensions
        Imgproc.putText(
            mat,
            "$label: ${"%.2f".format(dimensions.width)} cm x ${"%.2f".format(dimensions.height)} cm (${"%.2f".format(confidence)})",
            Point(50.0, 70.0 + index * 30.0),
            Imgproc.FONT_HERSHEY_SIMPLEX,
            0.6,
            Scalar(0.0, 255.0, 0.0),
            2
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
data class DetectedObject(val label: String, val dimensions: Dimensions, val confidence: Double)


fun detectObjectsAndDimensions(mat: Mat, scaleFactor: Double): List<DetectedObject> {
    // Carrega el model YOLO
    val modelWeights = "assets/yolo/yolov3.weights"
    val modelConfiguration = "assets/yolo/yolov3.cfg"
    val net: Net = Dnn.readNetFromDarknet(modelConfiguration, modelWeights)

    // Carrega les classes
    val classes = File("assets/yolo/coco.names").readLines()

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
            val classId = scores.argmax()
            val confidence = scores.get(0, classId)[0]

            if (confidence > 0.5) { // Filtra per confiança
                val centerX = detection.get(i, 0)[0] * mat.cols()
                val centerY = detection.get(i, 1)[0] * mat.rows()
                val width = detection.get(i, 2)[0] * mat.cols()
                val height = detection.get(i, 3)[0] * mat.rows()

                val x = (centerX - width / 2).toInt()
                val y = (centerY - height / 2).toInt()

                val objectWidthCm = width * scaleFactor
                val objectHeightCm = height * scaleFactor

                val label = classes[classId.toInt()]
                detectedObjects.add(DetectedObject(label, Dimensions(objectWidthCm, objectHeightCm), confidence))
            }
        }
    }

    return detectedObjects
}

fun main() {
    // Inicialitza OpenCV
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME)

    // Carrega la imatge
    val mat = Imgcodecs.imread("path_to_image.jpg")

    // Defineix el factor d'escala (per exemple, 0.1 significa que 1 píxel = 0.1 cm)
    val scaleFactor = 0.1

    // Detecta objectes i les seves dimensions
    val detectedObjects = detectObjectsAndDimensions(mat, scaleFactor)

    // Imprimeix els resultats
    for (obj in detectedObjects) {
        Log.d("OpenCV", "Objecte: ${obj.label}, Dimensions: ${obj.dimensions.width}x${obj.dimensions.height} cm, Confiança: ${obj.confidence}")
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

