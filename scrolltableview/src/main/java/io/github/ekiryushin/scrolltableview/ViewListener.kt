package io.github.ekiryushin.scrolltableview

import android.app.AlertDialog
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import io.github.ekiryushin.scrolltableview.cell.RowCell
import java.util.*

interface ViewListener {
    /** Задать обработчик клика по строке.
     * @param rowId индекс строки в данных, где расположена ячейка.
     * @param columnId индекс столбца в данных, где расположена ячейка.
     * @param row строка со значениями для редактирования.
     * @param view непосредственно ячейка, по которой нажали.
     */
    fun setRowClickListener(rowId: Int, columnId: Int, row: RowCell?, view: View? = null)

    /** Установить фон строки в зависимости от того удалена она или нет.
     * @param changeRowId индекс измененной строки.
     * @param isDeleted true строка удаленная, false - строка восстановленная.
     */
    fun setRowBackground(changeRowId: Int, isDeleted: Boolean)

    /** Установить фон ячейки, если значение в ней измененное.
     * @param changeRowId индекс строки, в которой изменилось значение.
     * @param changeColumnId индекст колонки, в которой изменилось значение.
     * @param isEdited true - значение изменилось на новое, false - значение изменилось в исходное.
     */
    fun setCellBackground(changeRowId: Int, changeColumnId: Int, isEdited: Boolean)

    /** Прокрутить список вниз. */
    fun scrollToEnd()

    /**
     * Показать окно для изменения значения.
     * @param builder подготовленное диалоговое окно.
     * @param view разметка, содержащая все поля ввода.
     */
    fun showDialog(builder: AlertDialog.Builder, view: LinearLayout)

    /**
     * Показать окно выбора даты.
     * @param editText поле ввода, для которого нужно показать окно.
     * @param gregorianCalendar дата, которую нужно выделить в календаре.
     */
    fun showDialogSelectDate(editText: EditText, gregorianCalendar: GregorianCalendar)

    /**
     * Удалить конкретную строку.
     * @param rowId идентификатор строки для удаления.
     */
    fun removeRow(rowId: Int)
}