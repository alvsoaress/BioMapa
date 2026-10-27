import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
    id("com.codingfeline.buildkonfig") version "0.22.0"
    id("org.jlleitschuh.gradle.ktlint")
    id("io.gitlab.arturbosch.detekt")
}

ktlint {
    android.set(true)
    ignoreFailures.set(false)
    filter {
        exclude { it.file.path.contains("/build/") }
    }
}

detekt {
    buildUponDefaultConfig = true
    allRules = false
    config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
    baseline = file("$rootDir/config/detekt/baseline.xml")
}

val datetimeCompat = "0.7.1-0.6.x-compat"

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { localProperties.load(it) }
}

repositories {
    google()
    mavenCentral()
}

kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }

    jvm()

    android {
        namespace = "org.example.biomapa.shared"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
        androidResources {
            enable = true
        }
        withHostTest {
            isIncludeAndroidResources = true
        }
        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(libs.compose.uiToolingPreview)          // ← androidx (Android)
            implementation(libs.compose.uiTooling)
            implementation("org.osmdroid:osmdroid-android:6.1.20")
        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            // --- CORRIGIDO: lifecycle e navigation alinhados na mesma geração de release ---
            implementation("org.jetbrains.androidx.lifecycle:lifecycle-viewmodel-compose:2.9.3")
            implementation("org.jetbrains.androidx.lifecycle:lifecycle-runtime-compose:2.9.3")
            api("org.jetbrains.androidx.navigation:navigation-compose:2.9.2")
            // --------------------------------------------------------------------------------

            implementation("org.jetbrains.kotlinx:kotlinx-datetime") {
                version { strictly(datetimeCompat) }
            }

            implementation("org.jetbrains.compose.material:material-icons-extended:1.7.3")

            implementation("io.github.jan-tennert.supabase:postgrest-kt:2.6.1")
            implementation("io.github.jan-tennert.supabase:gotrue-kt:2.6.1")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

            implementation("io.ktor:ktor-client-core:2.3.8")
            implementation("io.ktor:ktor-client-cio:2.3.8")

            implementation(libs.koin.core)
            implementation(libs.koin.compose)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.koin.test)
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}

configurations.all {
    resolutionStrategy {
        force("org.jetbrains.androidx.savedstate:savedstate:1.4.0")
    }
}

buildkonfig {
    packageName = "org.example.biomapa"

    defaultConfigs {
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "SUPABASE_URL",
            localProperties.getProperty("SUPABASE_URL", ""),
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "SUPABASE_ANON_KEY",
            localProperties.getProperty("SUPABASE_ANON_KEY", ""),
        )
    }
}
