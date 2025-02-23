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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
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

    implementation ("androidx.compose.ui:ui:1.4.0")
    implementation ("io.coil-kt:coil-compose:2.2.2") // Per carregar imatges des de URLs
    implementation ("androidx.activity:activity-compose:1.7.0")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")
//    PER REPRODUIUR VIDEOS A ANDROID
    implementation ("androidx.media3:media3-exoplayer:1.1.1")
    implementation ("androidx.media3:media3-ui:1.1.1")




}