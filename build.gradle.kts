import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(libs.bundles.plugins)
    }
}

allprojects {
    val javaVersion = JavaVersion.VERSION_17
    ext["jvm"] = javaVersion

    repositories {
        google()
        mavenCentral()
    }

    tasks.withType<JavaCompile> {
        sourceCompatibility = javaVersion.majorVersion
        targetCompatibility = javaVersion.majorVersion
    }
    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = javaVersion.majorVersion
    }
}
