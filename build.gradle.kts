// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
}

buildscript {
    repositories {
        // 优先使用阿里云镜像
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }

        // 腾讯云镜像作为备用
        maven { url = uri("https://mirrors.cloud.tencent.com/nexus/repository/maven-public/") }

        mavenCentral()
        google()
    }
}