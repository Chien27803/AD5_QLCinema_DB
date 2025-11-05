import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.ad5"
    compileSdk = 36

    buildFeatures {
        buildConfig = true   // ← Bật tạo BuildConfig.java
    }

    defaultConfig {
        applicationId = "com.example.ad5"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val properties = Properties()
        val localPropsFile = rootProject.file("local.properties")
        if (localPropsFile.exists()) {
            properties.load(localPropsFile.inputStream())
        } else {
            throw GradleException("local.properties not found!")
        }


        buildConfigField("String", "CLOUD_NAME", "\"${properties.getProperty("CLOUD_NAME")}\"")
        buildConfigField("String", "API_KEY", "\"${properties.getProperty("API_KEY")}\"")
        buildConfigField("String", "API_SECRET", "\"${properties.getProperty("API_SECRET")}\"")


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
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation("com.cloudinary:cloudinary-android-core:1.29.0")
    // Thư viện tải ảnh Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

}
