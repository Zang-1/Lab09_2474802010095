plugins {
    alias(libs.plugins.android.application)
    // Processes google-services.json (must be in app/)
    alias(libs.plugins.google.services)
    // Runs the Glide annotation processor
    alias(libs.plugins.ksp)
}

android {
    namespace = "vn.edu.vlu.lab10"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "vn.edu.vlu.lab10"   // MUST match the package name registered on Firebase
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    // View Binding generates ActivityAuthBinding, ActivityMainBinding, ItemPhotoBinding
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // AndroidX & Material Design 3
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.cardview)

    // Firebase: the BoM picks matching versions, so firebase-auth has no version number.
    // BoM 34 removed the -ktx artifacts; their Kotlin extensions now live in firebase-auth itself.
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.kotlinx.coroutines.android)

    // Glide: asynchronous image loading and caching
    implementation(libs.glide)
    ksp(libs.glide.ksp)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
