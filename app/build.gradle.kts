


plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("kapt")
    alias(libs.plugins.androidHilt)
    alias(libs.plugins.googleServices)

}

android {
    namespace = "com.deixebledenkaito.autotechmanuals"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.deixebledenkaito.autotechmanuals"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    repositories {
        google()
        mavenCentral()
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.tools.core)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.lifecycle)

    implementation(libs.vision.common)
    implementation(project(":sdk"))


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    // ===============================
    //FIREBASE
    // Import the Firebase BoM
    implementation(platform(libs.firebase.bom))
    // When using the BoM, don't specify versions in Firebase dependencies
    implementation(libs.firebase.database.ktx)
//    Firestore
    implementation(libs.firebase.firestore.ktx)
    //STORAGE
    implementation(libs.firebase.storage.ktx)
    //AUTINTIFICACIO
    implementation(libs.firebase.auth.ktx)
    //DATAsTORE
    implementation(libs.androidx.datastore.preferences)
    //    NAVIGATOR
    implementation(libs.firebase.messaging.ktx)
    //    REMOTE CONFIG
    implementation(libs.firebase.config.ktx)
//=======================================
    //DAGGER HILT
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation (libs.androidx.hilt.navigation.compose)
    // COIL ES PER IMPLEMENTAR IMATGES EN COMPOSE
    implementation(libs.coil.compose)

    implementation ("androidx.compose.ui:ui:1.7.8")
    implementation (libs.coil.compose.v222) // Per carregar imatges des de URLs
    implementation ("androidx.activity:activity-compose:1.10.1")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation ("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    implementation ("androidx.compose.runtime:runtime-livedata:1.7.8")
//    PER REPRODUIUR VIDEOS A ANDROID
    implementation ("androidx.media3:media3-exoplayer:1.5.1")
    implementation ("androidx.media3:media3-ui:1.5.1")

//    TENSORFLOW IMPLEMENTAR IMATGES AMB LES MIDES
    implementation (libs.tensorflow.lite)// Suport per a tasques com la detecció d'objectes
    implementation (libs.tensorflow.lite.support)// Biblioteca principal de TensorFlow Lite
    implementation (libs.tensorflow.lite.task.vision) // Tasques de visió per computador

    // Dependències de CameraX
    implementation ("androidx.camera:camera-core:1.3.0")
    implementation ("androidx.camera:camera-camera2:1.3.0")
    implementation ("androidx.camera:camera-lifecycle:1.3.0")
    implementation ("androidx.camera:camera-view:1.3.0")
    implementation ("androidx.camera:camera-extensions:1.3.0")
//Claude m'ha donat aquestes dependenceies'

    implementation ("com.google.mlkit:object-detection:17.0.2")
    implementation ("com.google.mlkit:vision-common:17.3.0")
    implementation ("com.google.mlkit:image-labeling:17.0.0")






}