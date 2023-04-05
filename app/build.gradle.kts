plugins {
    alias(libs.plugins.com.android.application)
}

android {
    namespace = "com.drhowdydoo.animenews"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.drhowdydoo.animenews"
        minSdk = 27
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        javaCompileOptions {
            annotationProcessorOptions {
                argument("primitiveTypeConverters","java.lang.String")
            }
        }

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8

    }

    buildFeatures {
        viewBinding = true
    }

}

dependencies {


    implementation(libs.androidx.room.runtime)
    implementation(libs.rxbinding.swiperefreshlayout)
    annotationProcessor(libs.androidx.room.compiler)
    implementation(libs.androidx.room.rxjava3)

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.glide)
    implementation(libs.converter.scalars)
    implementation(libs.adapter.rxjava3)
    implementation(libs.rxandroid)
    implementation(libs.rxjava)
    annotationProcessor(libs.compiler)
    implementation(libs.androidx.recyclerview)
    implementation(libs.retrofit)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.jsoup)
    implementation(libs.retrofit.converter)
    implementation(libs.annotation)
    implementation(libs.core)
    annotationProcessor(libs.processor)
    implementation(libs.converter.htmlescape)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)

}