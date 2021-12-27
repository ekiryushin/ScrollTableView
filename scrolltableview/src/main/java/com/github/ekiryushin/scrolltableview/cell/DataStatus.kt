package com.github.ekiryushin.scrolltableview.cell

/** Состояния значения ячейки или строки */
enum class DataStatus {
    /** Строка или значение без изменений */
    NORMAL,

    /** Измененное значение */
    EDIT,

    /** Добавленная строка или значение */
    ADD,

    /** Удаленная строка */
    DELETE
}