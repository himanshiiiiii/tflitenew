plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.tflite_integration_poc"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.tflite_integration_poc"
        minSdk = 24
        targetSdk = 34
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
    packaging {
        resources.excludes += "META-INF/**"
        resources.pickFirsts += "META-INF/LICENSE.txt"
        resources.excludes.add("**/*.tflite")
    }
}

dependencies {

    // tflite
    implementation ("org.tensorflow:tensorflow-lite:2.12.0")
    implementation ("org.tensorflow:tensorflow-lite-support:0.4.3")

    // Jetpack Compose dependencies
    implementation ("androidx.compose.ui:ui:1.4.3") // Latest Compose UI version
    implementation ("androidx.compose.material3:material3:1.1.0") // Material3 for Compose
    implementation ("androidx.compose.ui:ui-tooling-preview:1.4.3") // For preview
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.5.0") // Lifecycle extensions
//    implementation ("androidx.activity:activity-compose:1.7.0") // Activity Compose integration
    implementation ("androidx.compose.material:material:1.4.3") // Material Design for Compose
//    implementation ("androidx.compose.foundation:foundation:1.4.3") // Foundation components
    implementation ("androidx.compose.ui:ui-tooling:1.4.3")
    implementation ("androidx.compose.ui:ui:1.5.2") // Core UI
    implementation ("androidx.compose.material3:material3:1.2.0-alpha03") // Material3 Components
    implementation ("androidx.activity:activity-compose:1.7.2") // For Compose integration with Activity
    implementation ("androidx.compose.foundation:foundation:1.5.2") // Foundation for layouts and input fields
    implementation ("androidx.compose.ui:ui-text:1.5.2" )// Text and input options (KeyboardOptions is here)// UI tooling for debugging & previews



    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}