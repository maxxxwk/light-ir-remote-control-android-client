import org.gradle.kotlin.dsl.support.kotlinCompilerOptions
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.light.remote"
    compileSdk {
        version = release(37) {
            minorApiLevel = 0
        }
    }

    defaultConfig {
        applicationId = "com.light.remote"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0.0"
    }

    signingConfigs {
        create("release") {
            val localProperties = Properties()
            val localPropertiesFile = rootProject.file("local.properties")

            if (localPropertiesFile.exists()) {
                localProperties.load(localPropertiesFile.inputStream())

            }

            storeFile = file(localProperties.getProperty("signing.storeFile"))
            storePassword = localProperties.getProperty("signing.storePassword")
            keyAlias = localProperties.getProperty("signing.keyAlias")
            keyPassword = localProperties.getProperty("signing.keyPassword")
        }
    }



    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs["release"]
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs = listOf("-XXLanguage:+ExplicitBackingFields")
    }
}

androidComponents {
    onVariants { variant ->
        variant.outputs.filterIsInstance<com.android.build.api.variant.impl.VariantOutputImpl>()
            .forEach {
                it.outputFileName.set(
                    buildString {
                        append("Light_IR_Remote_Control").append('_')
                        append(variant.buildType).append('_')
                        append(variant.flavorName).append('_')
                        append(it.versionName.get())
                        append('(').append(it.versionCode.get()).append(')')
                        append(".apk")
                    }
                )
            }
    }
}

composeCompiler {
    reportsDestination = layout.buildDirectory.dir("compose_compiler")
    metricsDestination = layout.buildDirectory.dir("compose_compiler")
}

dependencies {
    implementation(libs.androidx.core.ktx)

    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation(libs.androidx.hilt.lifecycle.viewmodel.compose)

    implementation(platform(libs.okhttp.bom))
    implementation(libs.okhttp.core)
    implementation(libs.okhttp.logging)

    implementation(libs.androidx.splashscreen)

    implementation(libs.androidx.datastore)

    implementation(libs.compose.state.events)
}