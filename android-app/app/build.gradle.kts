plugins {
    id("com.android.application")
}

android {
    namespace = "dev.rebuild.arena"
    compileSdk = 37

    defaultConfig {
        applicationId = "dev.rebuild.arena"
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = "1.0.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
    }
}
