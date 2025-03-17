


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
    implementation(libs.testng)


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

    implementation (libs.ui)
    implementation (libs.coil.compose.v222) // Per carregar imatges des de URLs
    implementation (libs.androidx.activity.compose.v1101)
    implementation (libs.androidx.lifecycle.viewmodel.compose)
    implementation (libs.androidx.lifecycle.runtime.compose)
    //noinspection UseTomlInstead
    implementation ("androidx.compose.runtime:runtime-livedata:1.7.8")

    implementation (libs.androidx.runtime)
    implementation (libs.androidx.foundation)


//    PER REPRODUIUR VIDEOS A ANDROID
    implementation (libs.androidx.media3.exoplayer)
    implementation (libs.androidx.media3.ui)

//    TENSORFLOW IMPLEMENTAR IMATGES AMB LES MIDES
    implementation (libs.tensorflow.lite)// Suport per a tasques com la detecció d'objectes
    implementation (libs.tensorflow.lite.support)// Biblioteca principal de TensorFlow Lite
    implementation (libs.tensorflow.lite.task.vision) // Tasques de visió per computador

    // Dependències de CameraX
    implementation (libs.androidx.camera.core)
    implementation (libs.androidx.camera.camera2)
    implementation (libs.androidx.camera.lifecycle)
    implementation (libs.androidx.camera.view.v130)
    implementation (libs.androidx.camera.extensions)

//Claude m'ha donat aquestes dependenceies'
    implementation ("com.google.mlkit:object-detection:17.0.2")
    implementation (libs.vision.common)
    implementation (libs.image.labeling)

//json
    implementation(libs.gson)
//per carrregar imatges rapid
    implementation (libs.coil)

    implementation ("junit:junit:4.13.2")

    implementation ("androidx.work:work-runtime-ktx:2.8.1")
}

