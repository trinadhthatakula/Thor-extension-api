import com.vanniktech.maven.publish.AndroidSingleVariantLibrary
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.vanniktech.maven.publish)
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

// Vanniktech's signAllPublications() requires a GPG key for non-SNAPSHOT versions, including
// publishToMavenLocal. To verify publishing locally WITHOUT a key, append -PVERSION_NAME=1.0.0-SNAPSHOT
// (SNAPSHOT versions are exempt from signing). Real releases (VERSION_NAME=1.0.0) sign via the key in
// ~/.gradle/gradle.properties.
mavenPublishing {
    coordinates(
        groupId = providers.gradleProperty("GROUP").get(),
        artifactId = "thor-extension-api",
        version = providers.gradleProperty("VERSION_NAME").get()
    )
    configure(
        AndroidSingleVariantLibrary(
            variant = "release",
            sourcesJar = true,
            publishJavadocJar = true
        )
    )
    publishToMavenCentral(automaticRelease = true)
    signAllPublications()
    pom {
        name.set("Thor Extension API")
        description.set("Contract interfaces for building extensions for the Thor app manager.")
        inceptionYear.set("2026")
        url.set("https://github.com/trinadhthatakula/thor-extension-api")
        licenses {
            license {
                name.set("GNU General Public License v3.0 or later")
                url.set("https://www.gnu.org/licenses/gpl-3.0.txt")
                distribution.set("repo")
            }
        }
        developers {
            developer {
                id.set("trinadhthatakula")
                name.set("Trinadh Thatakula")
                url.set("https://github.com/trinadhthatakula")
            }
        }
        scm {
            url.set("https://github.com/trinadhthatakula/thor-extension-api")
            connection.set("scm:git:https://github.com/trinadhthatakula/thor-extension-api.git")
            developerConnection.set("scm:git:ssh://git@github.com/trinadhthatakula/thor-extension-api.git")
        }
    }
}
