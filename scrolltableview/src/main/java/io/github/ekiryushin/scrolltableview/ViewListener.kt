package io.github.ekiryushin.scrolltableview

import android.view.View
import io.github.ekiryushin.scrolltableview.cell.RowCell

interface ViewListener {
    /** Задать обработчик клика по строке.
     * @param rowId индекс строки в данных, где расположена ячейка
     * @param columnId индекс столбца в данных, где расположена ячейка
     * @param header заголовок таблицы
     * @param row строка со значениями для редактирования
     * @param countColumns количество колонок в таблице
     * @param view непосредственно ячейка, по которой нажали
     */
    fun setRowClickListener(
        rowId: Int,
        columnId: Int,
        header: RowCell?,
        row: RowCell?,
        countColumns: Int,
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