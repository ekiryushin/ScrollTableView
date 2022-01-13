package io.github.ekiryushin.scrolltableview

import android.app.AlertDialog
import android.content.Context
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.core.view.get
import androidx.core.view.isVisible
import com.google.android.material.textfield.TextInputLayout
import io.github.ekiryushin.scrolltableview.cell.CellView
import io.github.ekiryushin.scrolltableview.cell.DataStatus
import io.github.ekiryushin.scrolltableview.cell.RowCell
import io.github.ekiryushin.scrolltableview.extensions.*
import io.github.ekiryushin.scrolltableview.scrolled.ScrollTableParams
import io.github.ekiryushin.scrolltableview.utils.SVTConstants
import java.text.SimpleDateFormat
import java.util.*
import kotlin.streams.toList

/** Презентер отображения таблицы. */
class ScrollTableViewPresenter (
    private var params: ScrollTableParams,
    private val viewListener: ViewListener
) {

    //данные для отображения
    private var header: RowCell? = null
    private var data: MutableList<RowCell>? = null
    private var countColumns = 0

    /** Задать список заголовков.
     * @param header строка, содержащая ячейки для каждого столбца заголовка.
     */
    fun setHeader(header: RowCell) {
        this.header = header
    }

    /** Задать таблицу с данными без заголовка.
     * @param data список из строк, содержащих ячейки для каждого столбца.
     */
    fun setData(data: MutableList<RowCell>) {
        this.data = data
    }

    /** Добавить строку с данными.
     * @param row новая строка, содержащая ячейки для каждого столбца.
     */
    fun addRowData(
        inflater: LayoutInflater,
        tableFix: TableLayout,
        tableData: TableLayout,
        row: RowCell
    ) {
        data?.let { rows ->
            //проставляем статус на строку
            row.status = DataStatus.ADD
            //на все колонки задаем соответствующий статус
            row.also {
                it.columns.forEach { column -> column.status = DataStatus.ADD }
            }
            rows.add(row)
            //добавляем строку на экран
            val lastRowId = rows.size -1
            addRowInView(inflater, tableFix, tableData, lastRowId, row, true)
            viewListener.scrollToEnd()
        }
    }

    /** Построить таблицу с данными. */
    fun showTable(inflater: LayoutInflater, trHeaderFix: TableRow, trHeader: TableRow, tableFix: TableLayout, tableData: TableLayout) {
        //запонляем заголовки
        header?.let { row ->
            //добавляем столбец с иконкой удаления или восстановления
            if (params.enabledIconDelete) {
                trHeaderFix.addView(inflater.inflate(R.layout.stv_item_table_data_header, null))
            }

            countColumns = row.columns.size
            for (columnId in row.columns.indices) {
                val cell = row.columns[columnId]
                cell.value?.let {
                    //заголовки делаем без возможности редактирования
                    val view = inflater.inflate(R.layout.stv_item_table_data_header, null)
                    view.findViewById<TextView>(R.id.table_data_item_header).text = it
                    if (columnId < params.countFixColumn) {
                        trHeaderFix.addView(view)
                    }
                    else {
                        trHeader.addView(view)
                    }
                }
            }
        }

        //заполняем данные
        data?.let { rows ->
            for (ind in rows.indices) {
                val row = rows[ind]
                val tableRowFix = inflater.inflate(R.layout.stv_item_tablerow, null) as TableRow //строка закрепленных колонок

                //добавляем столбец с иконкой удаления или восстановления
                if (params.enabledIconDelete) {
                    val view = inflater.inflate(R.layout.stv_item_table_data_event, null)
                    setClickEventListener(ind, view)
                    tableRowFix.addView(view)
                }

                //добавляем строку на экран
                addRowInView(inflater, tableFix, tableData, ind, row)
            }
        }
    }

    //обновить значения в ячейке
    fun updateValue(rowId: Int, columnId: Int, value: String?) {
        data?.let { rows ->
            //находим нужныую ячейку и меняем в ней значение
            if (rowId < rows.size) {
                if (columnId < rows[rowId].columns.size) {
                    val cell = rows[rowId].columns[columnId]
                    cell.value = if (cell.viewed == CellView.EDIT_TIMESTAMP) {
                        value.toTimestamp()
                    }
                    else {
                        value
                    }

                    if (cell.value != cell.initialValue) {
                        if (cell.status != DataStatus.ADD) {
                            cell.status = DataStatus.EDIT
                        }
                        viewListener.setCellBackground(rowId, columnId, true)
                    }
                    else if (cell.status != DataStatus.ADD){
                        viewListener.setCellBackground(rowId, columnId, false)
                    }

                    //обновим обработчик клика
                    viewListener.setRowClickListener(rowId, columnId, rows[rowId])
                }
            }
        }
    }

    /** Получить все данные.
     * @return список строк, содержащих значения по каждому столбцу.
     */
    fun getHeader() = header?.copy()

    /** Получить все данные.
     * @return список строк, содержащих значения по каждому столбцу.
     */
    fun getData() = data?.map { row -> row.copy() }?.toList()

    /**
     * Подготовить диалоговое окно для редактирования данных.
     * @param context контекст activity
     * @param inflater
     * @param rowId идентификатор сторки, по которой было нажатие
     */
    fun builderEditDialog(context: Context?, inflater: LayoutInflater, rowId: Int) {
        val row = data?.get(rowId) ?: return

        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        val viewDialog = inflater.inflate(R.layout.stv_dialog_edit_view, null)
        val block = viewDialog.findViewById<LinearLayout>(R.id.edit_value_block)
        block.setTag(R.id.stv_tag_row_id, rowId)
        for (columnId in 0 until countColumns) {
            if (columnId <= row.columns.size) {
                val cell = row.columns[columnId]
                val view = inflater.inflate(R.layout.stv_item_edit_view, null)
                val editText: EditText = view.findViewById(R.id.edit_value)
                //задаем заголовок
                header?.columns?.get(columnId)?.value?.let { title ->
                    view.findViewById<TextInputLayout>(R.id.input_value).hint = title
                }
                //задаем значение
                setEditTextValue(editText, cell.viewed, cell.value)

                //задаем параметры, чтобы потом по ним обновить данные в таблице
                editText.setTag(R.id.stv_tag_column_id, columnId)
                block.addView(view)
            }
        }

        builder.setView(viewDialog)
        //настраиваем кнопки и показываем диалог
        viewListener.showDialog(builder, block)
    }

    /**
     * Отмена изменений значений.
     * @param rowId индентификатор строки, в которой отменили изменения.
     */
    fun canceledChangeValues(rowId: Int) {
        //новую строку удаляем
        data?.let {
            if (rowId< it.size && it[rowId].status == DataStatus.ADD) {
                it.removeAt(rowId)
                viewListener.removeRow(rowId)
            }
        }
    }

    /**
     * Заполнить поле ввода нужным значением в окне редактирования.
     * @param editText поле ввода.
     * @param viewed вариант отображения значения.
     * @param value значение для поля ввода.
     */
    private fun setEditTextValue(editText: EditText, viewed: CellView, value: String?) {
        when (viewed) {
            //ячейка для чтения
            CellView.ONLY_READ -> {
                editText.isEnabled = false
                editText.setText(value)
            }

            //ячейка должна быть с полем ввода числа
            CellView.EDIT_NUMBER -> {
                editText.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED
                editText.setTextIfValueAsFloat(value)
            }

            //ячейка должна быть с полем ввода даты
            CellView.EDIT_DD_MM_YYYY -> {
                setEditTextAsDate(editText, value)
            }
            CellView.EDIT_TIMESTAMP -> {
                val dateValue = value?.longToDDMMYYYY()
                setEditTextAsDate(editText, dateValue)
            }

            //ячейка должна быть с полем ввода текста
            else -> {
                editText.inputType = InputType.TYPE_TEXT_FLAG_AUTO_CORRECT
                editText.setText(value)
            }
        }
    }

    /**
     * Задать поле ввода как дату.
     * @param editText поле ввода.
     * @param value значение, которое нужно подставить в поле ввода.
     */
    private fun setEditTextAsDate(editText: EditText, value: String?) {
        //сформируем дату календаря
        val calendar = getGregorianCalendar(value)

        editText.isFocusableInTouchMode = false
        editText.isLongClickable = false
        editText.setOnClickListener { viewListener.showDialogSelectDate(editText, calendar) }
        editText.setTextIfValueAsDDMMYYYY(value)
    }

    /**
     * Сформировать грегорианский календарь из строки.
     * @param value строковая дата, из которой нужно сделать календарь.
     */
    private fun getGregorianCalendar(value: String?): GregorianCalendar {
        return if (value?.asDDMMYYYY() == true) {
            val date = SimpleDateFormat(SVTConstants.DATE_FORMAT, Locale.getDefault()).parse(value)
            date?.let {
                val calendar = Calendar.getInstance()
                calendar.time = it
                GregorianCalendar(
                    calendar[Calendar.YEAR],
                    calendar[Calendar.MONTH],
                    calendar[Calendar.DAY_OF_MONTH],
                    0,
                    0,
                    0)
            } ?: GregorianCalendar(TimeZone.getDefault())
        }
        else {
            GregorianCalendar(TimeZone.getDefault())
        }
    }

    //добавить строку на экран
    private fun addRowInView(inflater: LayoutInflater,
                             tableFix: TableLayout,
                             tableData: TableLayout,
                             rowId: Int,
                             row: RowCell,
                             rowAsNew: Boolean = false) {
        val tableRowFix = inflater.inflate(R.layout.stv_item_tablerow, null) as TableRow //строка закрепленных колонок
        val tableRowData = inflater.inflate(R.layout.stv_item_tablerow, null) as TableRow  //строка всех остальных колонок

        //добавляем столбец с иконкой удаления или восстановления
        if (params.enabledIconDelete) {
            val view = inflater.inflate(R.layout.stv_item_table_data_event, null)
            setClickEventListener(rowId, view)
            tableRowFix.addView(view)
        }

        //добавляем все колонки в нужные строки
        addColumns(inflater, rowId, row, tableRowFix, tableRowData, rowAsNew)

        //наполняем таблицы сформированными строками
        tableFix.addView(tableRowFix)
        tableData.addView(tableRowData)

        //сразу открываем окно ввода данных
        if (rowAsNew) {
            clickOnNewRow(tableRowFix, tableRowData)
        }
    }

    //навешать обработчики на иконки удаления и восстановления строки
    private fun setClickEventListener(rowId: Int, view: View?) {
        view?.let {
            val iconDelete: ImageView = it.findViewById(R.id.event_delete)
            val iconRestore: ImageView = it.findViewById(R.id.event_restore)

            iconDelete.setOnClickListener { icon ->
                removeRow(rowId)
                iconRestore.isVisible = true
                icon.isVisible = false
            }

            iconRestore.setOnClickListener { icon ->
                restoreRow(rowId)
                iconDelete.isVisible = true
                icon.isVisible = false
            }
        }
    }

    //удалить строку с данными по индексу строки
    private fun removeRow(position: Int) {
        data?.get(position)?.let {
            it.statusBeforeDelete = it.status
            it.status = DataStatus.DELETE
            viewListener.setRowBackground(position, true)
        }
    }

    //восстановить строку
    private fun restoreRow(position: Int) {
        data?.get(position)?.let {
            it.status = it.statusBeforeDelete
            viewListener.setRowBackground(position, false)
        }
    }

    //добавить колонки в строку закрепленных данных или строку обычных данных
    private fun addColumns(inflater: LayoutInflater,
                           rowId: Int,
                           row: RowCell,
                           tableRowFix: TableRow,
                           tableRowData: TableRow,
                           selectedCell: Boolean = false) {
        if (countColumns <= 0) {
            countColumns = row.columns.size
        }
        //обходим все столбцы с данными
        for (columnId in 0 until countColumns) {
            val view = inflater.inflate(R.layout.stv_item_table_data, null)

            if (columnId <= row.columns.size) {
                val cell = row.columns[columnId]

                val textView = view.findViewById<TextView>(R.id.table_data_item)
                cell.value?.let {
                    textView.text = if (cell.viewed == CellView.EDIT_TIMESTAMP) {
                        it.longToDDMMYYYY()
                    }
                    else {
                        it
                    }
                }

                //выделим цветом
                if (selectedCell) {
                    textView.setBackgroundResource(params.cellEditedColorResId)
                }
            }

            //навешиваем обработчик клика
            viewListener.setRowClickListener(rowId, columnId, row, view)

            if (columnId < params.countFixColumn) {
                tableRowFix.addView(view)
            } else {
                tableRowData.addView(view)
            }
        }
    }

    //нажатие по вновь созданной строке
    private fun clickOnNewRow(tableRowFix: TableRow, tableRowData: TableRow) {
        if (tableRowFix.childCount > 0) {
            tableRowFix[tableRowFix.childCount-1].callOnClick()
        }
        else if (tableRowData.childCount > 0) {
            tableRowData[0].callOnClick()
        }
    }
}