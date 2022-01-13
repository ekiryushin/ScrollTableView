plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    compileSdk = Config.COMPILE_SDK
    buildToolsVersion = Config.BUILD_TOOLS_VERSION
    defaultConfig {
        applicationId = Config.APPLICATION_ID
        minSdk = Config.MIN_SDK
        targetSdk = Config.TARGET_SDK
        versionCode = Config.VERSION_CODE
        versionName = Config.VERSION_NAME
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles (
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "../proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = Config.JVM_TARGET
    }

    viewBinding {
        android.buildFeatures.viewBinding = true
    }
}

dependencies {
    implementation(project(Module.SCROLL_TABLE_VIEW))

    implementation("androidx.core:core-ktx:${Version.KOTLIN_CORE}")

    implementation("androidx.appcompat:appcompat:${Version.APPCOMPAT}")
    implementation("androidx.constraintlayout:constraintlayout:${Version.CONSTRAIN}")
    implementation("com.google.android.material:material:${Version.MATERIAL}")
}