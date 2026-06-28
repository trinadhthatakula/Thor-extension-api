import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

android {
    namespace = "com.valhalla.thor.extension.api"
    compileSdk = libs.versions.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    // api: Compose types appear in public signatures (@Composable ConfigurationScreen, Modifier, ColorFilter).
    api(platform(libs.androidx.compose.bom))
    api(libs.androidx.ui)
    // api: backs the public AppIcon() helper so consumers can use it directly.
    api(libs.coil.compose)
}
