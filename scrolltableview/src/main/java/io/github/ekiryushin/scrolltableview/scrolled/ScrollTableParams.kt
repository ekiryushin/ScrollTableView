package io.github.ekiryushin.scrolltableview.scrolled

import io.github.ekiryushin.scrolltableview.R

/** Параметры для отображения таблицы. */
data class ScrollTableParams (
    /** Количество закрепленных колонок слева. */
    var countFixColumn: Int = 0,

    /** Показывать или нет иконку удаления строки. */
    var enabledIconDelete: Boolean = false,

    /** Цвет измененной ячейки. */
    var cellEditedColorResId: Int = R.color.stv_cell_edit_color,

    /** Стиль диалогового окна выбора даты. */
    var dialogStyleId: Int = R.style.STVDatePickerStyleDialog
)
