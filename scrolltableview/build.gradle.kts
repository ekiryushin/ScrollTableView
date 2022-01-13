import java.util.Properties
import java.io.FileInputStream

plugins {
    id("com.android.library")
    kotlin("android")
    id("maven-publish")
    id("org.jetbrains.dokka") version "1.4.20"
    signing
}

android {
    compileSdk = Config.COMPILE_SDK

    defaultConfig {
        minSdk = Config.MIN_SDK
        targetSdk = Config.TARGET_SDK
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles (
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "../proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = Config.JVM_TARGET
    }
}

val sourcesJar by tasks.creating(Jar::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    description = "Исходники библиотеки"
    archiveClassifier.set("sources")
    from(android.sourceSets.getByName("main").java.srcDirs)
}

val dokkaHtml by tasks.getting(org.jetbrains.dokka.gradle.DokkaTask::class)
val javadocJar: TaskProvider<Jar> by tasks.registering(Jar::class) {
    dependsOn(dokkaHtml)
    archiveClassifier.set("javadoc")
    from(dokkaHtml.outputDirectory)
}

artifacts {
    archives(sourcesJar)
    archives(javadocJar)
}

val props = Properties().apply {
    load(FileInputStream(File(rootProject.rootDir, "local.properties")))
}

//параметры подписи
project.ext["signing.keyId"] = props.getProperty("signing.keyId")
project.ext["signing.secretKeyRingFile"] = props.getProperty("signing.secretKeyRingFile")
project.ext["signing.password"] = props.getProperty("signing.password")

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                groupId = Publish.GROUP_ID
                artifactId = Publish.ARTIFACT_ID
                version = Publish.VERSION

                from(components["release"])

                artifact(sourcesJar)
                artifact(javadocJar)

                pom {
                    name.set("ScrollTableView")
                    description.set("ScrollTableView - это Android-библиоткека для отображение данных в виде прокручиваемой таблицы. "
                            + "С возможностью закрепления шапки таблицы и нескольких колонок слева. "
                            + "Измененные значения, добавленные или удаленные строки помечаются специальным статусом. "
                            + "Благодаря ему можно без труда обработать только те данные, "
                            + "которые добавил, изменил или удалил пользователь.")
                    url.set("https://github.com/ekiryushin/ScrollTableView")

                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }

                    developers {
                        developer {
                            id.set("ekiryushin")
                            name.set("Kiryushin Eugene")
                            email.set("kiryushin.ea@gmail.com")
                        }
                    }

                    scm {
                        url.set("https://github.com/ekiryushin/ScrollTableView")
                    }
                }
            }
        }

        repositories {
            maven {
                name = "sonatypeStaging"
                url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                credentials {
                    username = props.getProperty("ossrhUsername")
                    password = props.getProperty("ossrhPassword")
                }
            }
        }
    }

    signing {
        sign(publishing.publications["maven"])
    }
}

dependencies {
    implementation("androidx.core:core-ktx:${Version.KOTLIN_CORE}")
    implementation("com.google.android.material:material:${Version.MATERIAL}")
}
