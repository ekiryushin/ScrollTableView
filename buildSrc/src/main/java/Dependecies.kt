//параметры для сборки
object Config {
    const val APPLICATION_ID = "io.github.ekiryushin.scrolltableview.example"
    const val BUILD_TOOLS_VERSION = "30.0.2"
    const val COMPILE_SDK = 31
    const val MIN_SDK = 23
    const val TARGET_SDK = 31
    const val JVM_TARGET = "1.8"

    const val VERSION_CODE = 1
    const val VERSION_NAME = "1.0.1"
}

//версии подключаемых библиотек
object Version {
    const val MATERIAL = "1.4.0"
    const val FRAGMENT_KTX = "1.4.0"
    const val CONSTRAIN = "2.1.0"
    const val APPCOMPAT = "1.3.1"

    const val KOTLIN = "1.5.31"
    const val KOTLIN_CORE = "1.6.0"
    const val BUILD_GRADLE = "7.0.2"
    const val KOTLIN_GRADLE_PLUGIN = "1.5.20"
}

//модули приложения
object Module {
    const val SCROLL_TABLE_VIEW = ":scrolltableview"
}

//параметры для публикации библиотеки
object Publish {
    const val GROUP_ID = "io.github.ekiryushin"
    const val ARTIFACT_ID = "scrolltableview"
    const val VERSION = "1.0.3"
}