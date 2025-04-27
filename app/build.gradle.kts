plugins {
    alias(libs.plugins.android.application)
//    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
//    id("org.jetbrains.kotlin.kapt") // cần để dùng kapt
}

android {
    namespace = "com.example.chatapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.chatapp"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
//
//    kotlinOptions {
//        jvmTarget = "1.8" // thêm
//    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation(platform("com.google.firebase:firebase-bom:33.10.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation ("androidx.cardview:cardview:1.0.0") //thêm
    implementation ("com.google.firebase:firebase-auth")
    implementation ("com.google.firebase:firebase-database")

    implementation ("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")

    // Amplify
    implementation("com.amplifyframework:core:1.28.3")
    implementation("com.amplifyframework:aws-storage-s3:1.28.3")
    implementation("com.amplifyframework:aws-auth-cognito:1.28.2")

    //Websocket
    implementation("com.squareup.okhttp3:okhttp:4.10.0")

}
configurations.all {
    exclude(group = "com.google.android.play", module = "core-common")
}

