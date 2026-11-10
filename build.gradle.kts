// Project-level build file: plugins are only declared here ("apply false"), the app module applies them.
plugins {
    alias(libs.plugins.android.application) apply false
    // Google Services: reads app/google-services.json and generates the Firebase config resources
    alias(libs.plugins.google.services) apply false
    // KSP: runs the Glide annotation processor
    alias(libs.plugins.ksp) apply false
}
