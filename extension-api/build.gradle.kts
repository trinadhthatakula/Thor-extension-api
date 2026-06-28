import com.vanniktech.maven.publish.AndroidSingleVariantLibrary
import org.gradle.plugins.signing.SigningExtension
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

// DEVIATION FROM BRIEF (documented):
// Vanniktech 0.34.0's signAllPublications() makes signing *required* for any non-SNAPSHOT
// version (1.0.0 qualifies), so a credential-free `publishToMavenLocal` fails with
// "no configured signatory". The constraint requires publishToMavenLocal to NOT need GPG keys.
// Fix: gate signing on the presence of a signing key. Remote publishing supplies the
// in-memory key (signingInMemoryKey), so signed artifacts are still produced for Maven Central;
// only the credential-free local verification path is relaxed. signAllPublications() is kept.
plugins.withId("signing") {
    extensions.configure<SigningExtension> {
        val hasSigningKey = providers.gradleProperty("signingInMemoryKey").isPresent ||
            providers.gradleProperty("signing.keyId").isPresent ||
            providers.gradleProperty("signing.gnupg.keyName").isPresent
        setRequired({ hasSigningKey && gradle.taskGraph.allTasks.any { it.name.contains("PublishToMavenCentral") } })
    }
}
