package io.github.ekiryushin.scrolltableview.cell

/** Варианты отображения данных в ячейке */
enum class CellView {
    /** Только для чтения */
    ONLY_READ,

    /** Значение можно редактировать как строку */
    EDIT_STRING,

    /** Значение можно редактировать как число */
    EDIT_NUMBER
}