plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.printexpress"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.printexpress"
        minSdk = 26          // Android 8.0+ (needed for java.time dates)
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true   // generates ActivityLoginBinding etc. - no more findViewById
    }
}

dependencies {
    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.material)
    implementation(libs.swiperefreshlayout)

    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)
}
