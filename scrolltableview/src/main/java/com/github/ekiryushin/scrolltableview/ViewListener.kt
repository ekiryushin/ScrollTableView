package com.github.ekiryushin.scrolltableview

import android.view.View
import com.github.ekiryushin.scrolltableview.cell.CellView

interface ViewListener {
    /** Задать обработчик клика по значению.
     * @param rowId индекс строки в данных, где расположена ячейка
     * @param columnId индекс столбца в данных, где расположена ячейка
     * @param title заголовок для значения в окне редактирования
     * @param value значение ячейки в окне редактирования
     * @param viewed вариант отображения ячейки для корректной настройки окна редактирования
     * @param view непосредственно ячейка, по которой нажали
     */
    fun setValueClickListener(
        rowId: Int,
        columnId: Int,
        title: String?,
        value: String?,
        viewed: CellView,
        view: View? = null)

    /** Установить фон строки в зависимости от того удалена она или нет.
     * @param changeRowId индекс измененной строки
     * @param isDeleted true строка удаленная, false - строка восстановленная
     */
    fun setRowBackground(changeRowId: Int, isDeleted: Boolean)

    /** Установить фон ячейки, если значение в ней измененное
     * @param changeRowId индекс строки, в которой изменилось значение
     * @param changeColumnId индекст колонки, в которой изменилось значение
     * @param isEdited true - значение изменилось на новое, false - значение изменилось в исходное
     */
    fun setCellBackground(changeRowId: Int, changeColumnId: Int, isEdited: Boolean)

    /** Прокрутить список вниз */
    fun scrollToEnd()
}