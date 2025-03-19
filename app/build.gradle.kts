import org.apache.tools.ant.util.JavaEnvUtils.VERSION_1_8

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    kotlin("kapt")  // 添加这个插件
}

android {
    namespace = "com.sunnyweather.changqiongwaimai"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.sunnyweather.changqiongwaimai"
        minSdk = 28
        targetSdk = 31
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false // 开启代码混淆
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        buildFeatures {
            viewBinding = true
            dataBinding = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1") // 确保有这个依赖
    implementation("androidx.activity:activity-ktx:1.7.0") // 这个是提供 viewModels 扩展函数的库
//    kapt("com.github.bumptech.glide:compiler:4.16.0")
    implementation("androidx.fragment:fragment-ktx:1.5.5")  //在Fragment中使用viewModels 扩展函数
    implementation("de.hdodenhof:circleimageview:3.1.0") //圆形图片库

    kapt("com.github.bumptech.glide:compiler:4.16.0") // 如果需要使用Glide的注解处理器，启用这个依赖

    implementation("com.afollestad.material-dialogs:core:3.3.0")

}