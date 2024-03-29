import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    id("kotlin-kapt")
}

android {
    namespace = "com.example.github"
    compileSdk = 33

    defaultConfig {
        //api key 변수 지정
        buildConfigField("String", "API_KEY", getApiKey("api.key"))
        //url 변수 지정
        buildConfigField("String", "URL_WEATHER", getApiKey("url.weather"))
        applicationId = "com.example.github"
        minSdk = 24
        targetSdk = 33
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
    buildFeatures {
        viewBinding = true
        dataBinding = true
        buildConfig = true

    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

}

fun getApiKey(propertyKey: String): String {
   return gradleLocalProperties(rootDir).getProperty(propertyKey)
}

    dependencies {

        implementation("androidx.core:core-ktx:1.9.0")
        implementation("androidx.appcompat:appcompat:1.6.1")
        implementation("com.google.android.material:material:1.8.0")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.0")
        implementation("com.github.bumptech.glide:glide:4.12.0")
        implementation("com.google.firebase:firebase-database:20.3.0")
        implementation("com.google.firebase:firebase-auth:22.3.1")
        //implementation("com.squareup.okhttp3:logging-interceptor:4.9.1")
        annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")

        //firebase
        implementation(platform("com.google.firebase:firebase-bom:32.7.1"))
        implementation("com.google.firebase:firebase-analytics:21.5.0")
        implementation("com.google.firebase:firebase-auth-ktx:22.3.1")
        implementation("com.google.firebase:firebase-database-ktx:20.3.0")
        implementation("com.google.firebase:firebase-storage:20.3.0")
        implementation ("com.google.firebase:firebase-core:20.1.2")
        implementation ("com.google.firebase:firebase-storage:20.0.1")

        //Splash
        implementation("androidx.core:core-splashscreen:1.0.1")

        // GlideApp
        implementation ("com.github.bumptech.glide:glide:4.12.0")
        implementation ("com.firebaseui:firebase-ui-storage:7.2.0")
        implementation ("com.google.firebase:firebase-storage:20.2.0")
        kapt ("com.github.bumptech.glide:compiler:4.12.0")


        implementation("androidx.constraintlayout:constraintlayout:2.1.4")
        implementation("com.google.code.gson:gson:2.9.0")
        // retrofit2
        implementation("com.squareup.retrofit2:retrofit:2.9.0")
        implementation("com.squareup.retrofit2:converter-gson:2.9.0")

        // 위치
        implementation("com.google.android.gms:play-services-location:19.0.1")
        testImplementation("junit:junit:4.13.2")
        androidTestImplementation("androidx.test.ext:junit:1.1.5")
        androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")


        }


