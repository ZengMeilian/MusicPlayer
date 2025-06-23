// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.1" apply false
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.20") // 使用最新稳定版
        classpath("com.android.tools.build:gradle:8.1.3") // 确保与Gradle版本兼容
    }
}