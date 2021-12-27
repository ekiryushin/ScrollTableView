buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:${Version.BUILD_GRADLE}")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Version.KOTLIN_GRADLE_PLUGIN}")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url = java.net.URI("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven") }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}