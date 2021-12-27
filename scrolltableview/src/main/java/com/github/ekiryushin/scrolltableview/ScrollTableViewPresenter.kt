package com.github.ekiryushin.scrolltableview

import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.view.isVisible
import com.github.ekiryushin.scrolltableview.cell.CellView
import com.github.ekiryushin.scrolltableview.cell.DataStatus
import com.github.ekiryushin.scrolltableview.cell.RowCell

/** Презентер отображения таблицы */
class ScrollTableViewPresenter (
    private var params: ScrollTableParams,
    private val viewListener: ViewListener) {

    //данные для отображения
    private var headers: RowCell? = null
    private var data: MutableList<RowCell>? = null
    private var countColumns = 0

    /** Задать список заголовков.
     * @param headers строка, содержащая ячейки для каждого столбца заголовка
     */
    fun setHeaders(headers: RowCell) {
        this.headers = headers
    }

    /** Задать таблицу с данными без заголовка
     * @param data список из строк, содержащих ячейки для каждого столбца
     */
    fun setData(data: MutableList<RowCell>) {
        this.data = data
    }

    /** Добавить строку с данными.
     * @param row новая строка, содержащая ячейки для каждого столбца
     */
    fun addRowData(
        inflater: LayoutInflater,
        tableFix: TableLayout,
        tableData: TableLayout,
        row: RowCell) {
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

    /** Построить таблицу с данными */
    fun showTable(inflater: LayoutInflater, trHeaderFix: TableRow, trHeader: TableRow, tableFix: TableLayout, tableData: TableLayout) {
        //запонляем заголовки
        headers?.let { row ->
            //добавляем столбец с иконкой удаления или восстановления
            if (params.enabledIconDelete) {
                trHeaderFix.addView(inflater.inflate(R.layout.item_table_data_header, null))
            }

            countColumns = row.columns.size
            for (columnId in row.columns.indices) {
                val cell = row.columns[columnId]
                cell.value?.let {
                    //заголовки делаем без возможности редактирования
                    val view = inflater.inflate(R.layout.item_table_data_header, null)
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
                val tableRowFix = inflater.inflate(R.layout.item_tablerow, null) as TableRow //строка закрепленных колонок

                //добавляем столбец с иконкой удаления или восстановления
                if (params.enabledIconDelete) {
                    val view = inflater.inflate(R.layout.item_table_data_event, null)
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
                    cell.value = value
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
                    if(cell.viewed != CellView.ONLY_READ) {
                        viewListener.setValueClickListener(
                            rowId,
                            columnId,
                            headers?.columns?.get(columnId)?.value,
                            value,
                            cell.viewed)
                    }
                }
            }
        }
    }

    //получить все данные
    fun getData() = data

    //добавить строку на экран
    private fun addRowInView(
        inflater: LayoutInflater,
        tableFix: TableLayout,
        tableData: TableLayout,
        rowId: Int,
        row: RowCell,
        selectedCell: Boolean = false) {
        val tableRowFix = inflater.inflate(R.layout.item_tablerow, null) as TableRow //строка закрепленных колонок
        val tableRowData = inflater.inflate(R.layout.item_tablerow, null) as TableRow  //строка всех остальных колонок

        //добавляем столбец с иконкой удаления или восстановления
        if (params.enabledIconDelete) {
            val view = inflater.inflate(R.layout.item_table_data_event, null)
            setClickEventListener(rowId, view)
            tableRowFix.addView(view)
        }

        //добавляем все колонки в нужные строки
        addColumns(inflater, rowId, row, tableRowFix, tableRowData, selectedCell)

        //наполняем таблицы сформированными строками
        tableFix.addView(tableRowFix)
        tableData.addView(tableRowData)
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
    private fun addColumns(
        inflater: LayoutInflater,
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
            val view = inflater.inflate(R.layout.item_table_data, null)
            var viewed = CellView.EDIT_STRING
            var value: String? = null

            if (columnId <= row.columns.size) {
                val cell = row.columns[columnId]

                val textView = view.findViewById<TextView>(R.id.table_data_item)
                cell.value?.let { textView.text = it }
                value = cell.value
                viewed = cell.viewed

                //выделим цветом
                if (selectedCell) {
                    textView.setBackgroundResource(params.cellEditedColorResId)
                }
            }

            //навешиваем обработчик клика
            if(viewed != CellView.ONLY_READ) {
                viewListener.setValueClickListener(
                    rowId,
                    columnId,
                    headers?.columns?.get(columnId)?.value,
                    value,
                    viewed,
                    view)
            }

            if (columnId < params.countFixColumn) {
                tableRowFix.addView(view)
            } else {
                tableRowData.addView(view)
            }
        }
    }
}