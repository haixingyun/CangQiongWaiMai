
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    kotlin("kapt")
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

    buildFeatures {
        viewBinding = true
    }

    buildTypes {
        release {
            isMinifyEnabled = true    // 开启混淆和压缩
            isShrinkResources = true  // 移除无用资源
            //这段代码是用于配置 ProGuard / R8 混淆规则文件，目的是在构建 APK 的 Release 版本时进行：
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), //这是 Google 提供的默认混淆规则文件
                "proguard-rules.pro"     //这是你自己写的自定义混淆规则文件
            )
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
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    // 这个是提供 viewModels 扩展函数的库
    implementation("androidx.activity:activity-ktx:1.7.0")
    //在Fragment中使用viewModels 扩展函数
    implementation("androidx.fragment:fragment-ktx:1.5.5")
    //圆形图片库
    implementation("de.hdodenhof:circleimageview:3.1.0")
    // ViewPager2
    implementation ("androidx.viewpager2:viewpager2:1.0.0")
    // Material Components (包含 TabLayout)
    implementation ("com.google.android.material:material:1.4.0")
    // 吐司框架：https://github.com/getActivity/Toaster
    implementation("com.github.getActivity:Toaster:12.8")
}