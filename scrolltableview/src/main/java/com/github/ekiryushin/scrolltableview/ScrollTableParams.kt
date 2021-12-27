package com.github.ekiryushin.scrolltableview

/** Параметры для отображения таблицы. */
data class ScrollTableParams (
    /** Количество закрепленных колонок слева */
    var countFixColumn: Int = 0,

    /** Показывать или нет иконку удаления строки */
    var enabledIconDelete: Boolean = false,

    /** Цвет измененной ячейки */
    var cellEditedColorResId: Int = R.color.cell_edit_color
)
